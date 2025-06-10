package fixture.core.domain;

import core.domain.member.entity.Member;

public class MemberFixture {
    public static final String EMAIL = "email@email.com";
    public static final String PASSWORD = "password";
    public static final int AGE = 20;

    public static Member getMember() {
        return new Member(EMAIL, PASSWORD, AGE);
    }
}
