package com.burntoburn.easyshift.scheduler;

import com.burntoburn.easyshift.entity.schedule.Shift;
import com.burntoburn.easyshift.entity.user.User;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Component
public class AutoAssignmentScheduler {

    /**
     * Shift 목록에 대해 자동 배정을 수행합니다.
     * <p>
     * 알고리즘 흐름:
     * 1. 후보 큐를 초기화하여 유저들을 초기 순서(인덱스 기준)로 저장합니다.
     * 2. 각 shift에 대해:
     * a. 후보 큐가 비면 전체 유저로 재초기화합니다.
     * b. 후보 큐에서 가능한 유저를 찾습니다.
     * - 만약 첫 후보가 마지막 배정된 유저와 같으면 waitingPool에 옮깁니다.
     * - 유저가 해당 shift에 휴무라면 waitingPool으로 옮깁니다.
     * c. 조건에 맞는 유저가 있으면 배정하고, 없으면 waitingPool에서 강제 배정합니다.
     * d. waitingPool에 있던 후보들을 후보 큐로 복구합니다.
     */
    public List<Pair<Long, Long>> assignShifts(ShiftAssignmentData assignmentData) {
        List<Shift> shifts = assignmentData.shifts();
        List<User> users = assignmentData.users();
        Map<User, Set<LocalDate>> userLeaveDates = assignmentData.userLeaveDates();

        // 배정 결과 저장 리스트 (Shift ID, User ID)
        List<Pair<Long, Long>> assignments = new ArrayList<>();

        // (1) 후보 큐 초기화 (초기 순서를 유지)
        PriorityQueue<UserPair> candidateQueue = initializeCandidateQueue(users);
        List<UserPair> waitingPool = new ArrayList<>();

        User lastAssignedUser = null; // 마지막으로 배정된 유저
        // (2) 각 shift에 대해 배정 진행
        for (Shift shift : shifts) {
            // 2-a. 후보 큐가 비어있다면 전체 유저로 다시 초기화
            if (candidateQueue.isEmpty()) {
                candidateQueue = initializeCandidateQueue(users);
            }

            // 2-b. 후보 큐에서 가능한 유저 찾기
            User assignedUser = findAvailableUserForShift(shift, candidateQueue, waitingPool, lastAssignedUser, userLeaveDates, users);

            // 2-c. 가능한 유저가 없으면 waitingPool에서 강제 배정
            if (assignedUser == null) {
                assignedUser = forceAssignUser(waitingPool);
            }
            shift.assignUser(assignedUser);
//            assignments.add(Pair.of(assignedUser.getId(), shift.getId())); // (User ID, Shift ID) 쌍 추가
            lastAssignedUser = assignedUser;

            // 2-d. waitingPool에 있던 후보들을 후보 큐로 복구 (ArrayList 사용 시 remove(0))
            reinsertWaitingPool(candidateQueue, waitingPool);
        }
        return assignments; // 배정 결과 반환
    }

    /**
     * (1) 초기 후보 큐를 생성합니다.
     * 각 유저를 인덱스와 함께 UserPair로 만들어 우선순위 큐에 추가합니다.
     */
    private PriorityQueue<UserPair> initializeCandidateQueue(List<User> users) {
        PriorityQueue<UserPair> queue = new PriorityQueue<>();
        for (int i = 0; i < users.size(); i++) {
            queue.offer(new UserPair(i, users.get(i)));
        }
        return queue;
    }

    /**
     * (2-b) 후보 큐에서 가능한 유저를 찾습니다.
     * - 만약 후보가 새 사이클의 첫 후보(마지막 배정된 유저와 같고, 후보 큐 크기가 전체 유저 수 - 1인 경우)면 waitingPool에 저장합니다.
     * - 후보가 shift의 휴무일이면 waitingPool으로 옮깁니다.
     * - 조건을 모두 통과한 후보를 반환합니다.
     */
    private User findAvailableUserForShift(Shift shift,
                                           PriorityQueue<UserPair> candidateQueue,
                                           List<UserPair> waitingPool,
                                           User lastAssignedUser,
                                           Map<User, Set<LocalDate>> userLeaveDates,
                                           List<User> users) {
        User assignedUser = null;
        while (!candidateQueue.isEmpty()) {
            UserPair candidatePair = candidateQueue.poll();
            User candidate = candidatePair.getUser();

            // 조건 1: 새 사이클의 첫 후보인지 확인
            if (isFirstCandidateSameAsLastAssigned(candidate, lastAssignedUser, candidateQueue.size(), users.size())) {
                waitingPool.add(candidatePair);
                continue;
            }

            // 조건 2: 휴무일이면 waitingPool으로 이동
            if (isOnLeave(candidate, shift.getShiftDate(), userLeaveDates)) {
                waitingPool.add(candidatePair);
                continue;
            }

            // 조건을 모두 통과하면 이 후보를 배정합니다.
            assignedUser = candidate;
            break;
        }
        return assignedUser;
    }

    /**
     * (2-c) waitingPool에 있는 유저 중에서 강제 배정을 수행합니다.
     * waitingPool의 첫 번째 유저를 꺼내 강제 배정합니다.
     */
    private User forceAssignUser(List<UserPair> waitingPool) {
        UserPair forcedCandidatePair = waitingPool.removeFirst();
        User forcedCandidate = forcedCandidatePair.getUser();
        return forcedCandidate;
    }

    /**
     * (2-d) waitingPool에 있는 모든 후보들을 후보 큐에 복구합니다.
     */
    private void reinsertWaitingPool(PriorityQueue<UserPair> candidateQueue, List<UserPair> waitingPool) {
        while (!waitingPool.isEmpty()) {
            candidateQueue.offer(waitingPool.removeFirst()); // ArrayList에서 첫 요소를 꺼내 재삽입
        }
    }

    /**
     * 새 사이클의 첫 후보인지 확인합니다.
     * - 후보 큐가 전체 유저 수(users.size())로 채워진 상태에서,
     * 첫 pop된 후보의 경우 candidateQueue.size()가 totalUsers - 1이 됩니다.
     * - 이 때, 후보가 마지막 배정된 유저(lastAssignedUser)와 동일하면 true를 반환합니다.
     */
    private boolean isFirstCandidateSameAsLastAssigned(User candidate, User lastAssignedUser, int currentQueueSize, int totalUsers) {
        return lastAssignedUser != null && candidate.equals(lastAssignedUser) && currentQueueSize == totalUsers - 1;
    }

    /**
     * 주어진 candidate가 shiftDate에 휴무인지 확인합니다.
     */
    private boolean isOnLeave(User candidate, LocalDate shiftDate, Map<User, Set<LocalDate>> userLeaveDates) {
        return userLeaveDates.containsKey(candidate) && userLeaveDates.get(candidate).contains(shiftDate);
    }
}
