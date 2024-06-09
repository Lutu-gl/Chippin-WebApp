package at.ac.tuwien.sepr.groupphase.backend.basetest;

import at.ac.tuwien.sepr.groupphase.backend.entity.Activity;
import at.ac.tuwien.sepr.groupphase.backend.entity.ActivityCategory;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Category;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.entity.Pantry;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface TestData {
    String BASE_URI = "/api/v1";
    String ADMIN_USER = "admin@email.com";
    List<String> ADMIN_ROLES = new ArrayList<>() {
        {
            add("ROLE_ADMIN");
            add("ROLE_USER");
        }
    };
    String DEFAULT_USER = "admin@email.com";
    List<String> USER_ROLES = new ArrayList<>() {
        {
            add("ROLE_USER");
        }
    };

    List<ApplicationUser> USERS = new ArrayList<>() {
        {
            add(ApplicationUser.builder()
                .email("testu3@email.com")
                .password("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG")
                .build()
            );
            add(ApplicationUser.builder()
                .email("testu4@email.com")
                .password("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG")
                .build()
            );
            // Add more users similarly
            add(ApplicationUser.builder()
                .email("testu5@email.com")
                .password("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG")
                .build()
            );
            add(ApplicationUser.builder()
                .email("testu6@email.com")
                .password("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG")
                .build()
            );
            add(ApplicationUser.builder()
                .email("testu7@email.com")
                .password("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG")
                .build()
            );
            add(ApplicationUser.builder()
                .email("testu8@email.com")
                .password("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG")
                .build()
            );
            add(ApplicationUser.builder()
                .email("testu9@email.com")
                .password("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG")
                .build()
            );
            add(ApplicationUser.builder()
                .email("testu10@email.com")
                .password("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG")
                .build()
            );
            add(ApplicationUser.builder()
                .email("testu11@email.com")
                .password("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG")
                .build()
            );
            add(ApplicationUser.builder()
                .email("testu12@email.com")
                .password("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG")
                .build()
            );
        }
    };

    List<GroupEntity> GROUPS = new ArrayList<>() {
        {
            add(GroupEntity.builder()
                .groupName("testGroup1")
                .users(new HashSet<>(Arrays.asList(USERS.get(0), USERS.get(1), USERS.get(2))))
                .build());

            add(GroupEntity.builder()
                .groupName("testGroup2")
                .users(new HashSet<>(Arrays.asList(USERS.get(3), USERS.get(0), USERS.get(5), USERS.get(6))))
                .build());

            add(GroupEntity.builder()
                .groupName("testGroup3")
                .users(new HashSet<>(Arrays.asList(USERS.get(0), USERS.get(8), USERS.get(1), USERS.get(4), USERS.get(7))))
                .build());
        }
    };

    List<Pantry> PANTRIES = new ArrayList<>() {
        {
            Pantry p0 = Pantry.builder().build();
            GroupEntity g0 = GROUPS.get(0);
            p0.setGroup(g0);
            g0.setPantry(p0);
            add(p0);

            Pantry p1 = Pantry.builder().build();
            GroupEntity g1 = GROUPS.get(1);
            p1.setGroup(g1);
            g1.setPantry(p1);
            add(p1);

            Pantry p2 = Pantry.builder().build();
            GroupEntity g2 = GROUPS.get(2);
            p2.setGroup(g2);
            g2.setPantry(p2);
            add(p2);
        }
    };

    List<Expense> EXPENSES = new ArrayList<>() {
        {
            add(Expense.builder()
                .name("testExpense1")
                .category(Category.Food)
                .amount(500.0)
                .date(LocalDateTime.now().minus(1, java.time.temporal.ChronoUnit.DAYS))
                .payer(USERS.get(0))
                .group(GROUPS.get(0))
                .participants(Map.of(USERS.get(0), 0.1, USERS.get(1), 0.9))
                .build());

            add(Expense.builder()
                .name("testExpense2")
                .category(Category.Travel)
                .amount(100.0)
                .date(LocalDateTime.now().minus(2, java.time.temporal.ChronoUnit.DAYS))
                .payer(USERS.get(3))
                .group(GROUPS.get(1))
                .participants(Map.of(USERS.get(3), 1.0))
                .build());

            add(Expense.builder()
                .name("testExpense3")
                .category(Category.Entertainment)
                .amount(200.0)
                .date(LocalDateTime.now().minus(4, java.time.temporal.ChronoUnit.DAYS))
                .payer(USERS.get(8))
                .group(GROUPS.get(2))
                .participants(Map.of(USERS.get(8), 0.5, USERS.get(0), 0.5))
                .build());

            // Add more expenses similarly
            add(Expense.builder()
                .name("testExpense4")
                .category(Category.Travel)
                .amount(300.0)
                .date(LocalDateTime.now().minus(5, java.time.temporal.ChronoUnit.DAYS))
                .payer(USERS.get(4))
                .group(GROUPS.get(1))
                .participants(Map.of(USERS.get(3), 0.25, USERS.get(0), 0.25, USERS.get(5), 0.5))
                .build());

            add(Expense.builder()
                .name("testExpense5")
                .category(Category.Other)
                .amount(150.0)
                .date(LocalDateTime.now().minus(8, java.time.temporal.ChronoUnit.DAYS))
                .payer(USERS.get(9))
                .group(GROUPS.get(2))
                .participants(Map.of(USERS.get(8), 0.3, USERS.get(9), 0.7))
                .build());

        }
    };

    List<Activity> ACTIVITY = new ArrayList<>() {
        {
            add(Activity.builder()
                .category(ActivityCategory.EXPENSE)
                .timestamp(LocalDateTime.now().minus(1, java.time.temporal.ChronoUnit.DAYS))
                .expense(EXPENSES.get(0))
                .build());

            add(Activity.builder()
                .category(ActivityCategory.EXPENSE)
                .timestamp(LocalDateTime.now().minus(3, java.time.temporal.ChronoUnit.DAYS))
                .expense(EXPENSES.get(1))
                .build());

            add(Activity.builder()
                .category(ActivityCategory.EXPENSE)
                .timestamp(LocalDateTime.now().minus(4, java.time.temporal.ChronoUnit.DAYS))
                .expense(EXPENSES.get(2))
                .build());

            add(Activity.builder()
                .category(ActivityCategory.EXPENSE)
                .timestamp(LocalDateTime.now().minus(7, java.time.temporal.ChronoUnit.DAYS))
                .expense(EXPENSES.get(3))
                .build());

            add(Activity.builder()
                .category(ActivityCategory.EXPENSE)
                .timestamp(LocalDateTime.now().minus(8, java.time.temporal.ChronoUnit.DAYS))
                .expense(EXPENSES.get(4))
                .build());
        }
    };
}
