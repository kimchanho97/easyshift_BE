package test;

import com.burntoburn.easyshift.entity.user.Role;
import com.burntoburn.easyshift.entity.user.User;

import java.time.LocalDate;
import java.util.*;

public class UserTestDataGenerator {

    /**
     * 주어진 유저 수만큼 Worker 유저를 생성
     *
     * @param userCount 생성할 유저 수
     * @return 유저 리스트
     */
    public static List<User> generateUsers(int userCount) {
        List<User> users = new ArrayList<>();

        for (int i = 1; i <= userCount; i++) {
            users.add(User.builder()
                    .id((long) i)
                    .name("User" + i)
                    .email("user" + i + "@example.com")
                    .role(Role.WORKER)
                    .build());
        }
        return users;
    }

    /**
     * 특정 월(2025년 4월) 내에서 유저별 랜덤 휴무일을 생성
     *
     * @param users            휴무일을 설정할 유저 리스트
     * @param leaveDaysPerUser 한 유저당 휴무일 개수
     * @return 유저별 휴무일 Map
     */
    public static Map<User, Set<LocalDate>> generateUserLeaveDates(List<User> users, int leaveDaysPerUser) {
        Map<User, Set<LocalDate>> leaveDates = new HashMap<>();
        Random random = new Random();
        LocalDate startDate = LocalDate.of(2025, 4, 1);

        for (User user : users) {
            Set<LocalDate> dates = new HashSet<>();
            while (dates.size() < leaveDaysPerUser) {
                LocalDate randomDate = startDate.plusDays(random.nextInt(30)); // 2025년 4월 기준 랜덤 날짜
                dates.add(randomDate);
            }
            leaveDates.put(user, dates);
        }
        return leaveDates;
    }
}