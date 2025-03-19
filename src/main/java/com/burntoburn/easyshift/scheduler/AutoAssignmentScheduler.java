package com.burntoburn.easyshift.scheduler;

import com.burntoburn.easyshift.entity.schedule.Shift;
import com.burntoburn.easyshift.entity.user.User;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Component
public class AutoAssignmentScheduler {

    public List<Pair<Long, Long>> assignShifts(ShiftAssignmentData assignmentData) {
        List<Shift> shifts = assignmentData.shifts();
        List<User> users = assignmentData.users();
        Map<User, Set<LocalDate>> userLeaveDates = assignmentData.userLeaveDates();
        // (User ID, Shift ID) 쌍을 저장할 리스트 -> batch update를 위해 사용
        List<Pair<Long, Long>> assignments = new ArrayList<>();

        PriorityQueue<UserPair> candidateQueue = initializeCandidateQueue(users);
        List<UserPair> waitingPool = new ArrayList<>();

        User lastAssignedUser = null;
        for (Shift shift : shifts) {
            if (candidateQueue.isEmpty()) {
                candidateQueue = initializeCandidateQueue(users);
            }

            User assignedUser = findAvailableUserForShift(shift, candidateQueue, waitingPool, lastAssignedUser, userLeaveDates, users);
            if (assignedUser == null) {
                assignedUser = forceAssignUser(waitingPool);
            }
//            shift.assignUser(assignedUser);
            assignments.add(Pair.of(assignedUser.getId(), shift.getId())); // (User ID, Shift ID) 쌍 추가
            lastAssignedUser = assignedUser;
            reinsertWaitingPool(candidateQueue, waitingPool);
        }
        return assignments; // 배정 결과 반환
    }

    private PriorityQueue<UserPair> initializeCandidateQueue(List<User> users) {
        PriorityQueue<UserPair> queue = new PriorityQueue<>();
        for (int i = 0; i < users.size(); i++) {
            queue.offer(new UserPair(i, users.get(i)));
        }
        return queue;
    }

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
            // 조건 1: 가장 마지막에 배정된 유저와 동일한 경우, waitingPool에 추가
            if (isFirstCandidateSameAsLastAssigned(candidate, lastAssignedUser, candidateQueue.size(), users.size())) {
                waitingPool.add(candidatePair);
                continue;
            }
            // 조건 2: 휴무일인 경우, waitingPool에 추가
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

    private User forceAssignUser(List<UserPair> waitingPool) {
        UserPair forcedCandidatePair = waitingPool.removeFirst();
        return forcedCandidatePair.getUser();
    }

    private void reinsertWaitingPool(PriorityQueue<UserPair> candidateQueue, List<UserPair> waitingPool) {
        while (!waitingPool.isEmpty()) {
            candidateQueue.offer(waitingPool.removeFirst());
        }
    }

    private boolean isFirstCandidateSameAsLastAssigned(User candidate, User lastAssignedUser, int currentQueueSize, int totalUsers) {
        return lastAssignedUser != null && candidate.equals(lastAssignedUser) && currentQueueSize == totalUsers - 1;
    }

    private boolean isOnLeave(User candidate, LocalDate shiftDate, Map<User, Set<LocalDate>> userLeaveDates) {
        return userLeaveDates.containsKey(candidate) && userLeaveDates.get(candidate).contains(shiftDate);
    }
}
