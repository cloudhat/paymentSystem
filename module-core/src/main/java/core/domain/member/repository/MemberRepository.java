package core.domain.member.repository;

import core.domain.member.entity.Member;
import core.domain.member.entity.address.Address;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static core.domain.member.entity.QMember.member;
import static core.domain.member.entity.address.QAddress.address;


@RequiredArgsConstructor
@Repository
public class MemberRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Transactional
    public Member save(Member member) {
        em.persist(member);

        return member;
    }

    public Optional<Member> findByEmail(String email) {
        return Optional.ofNullable(queryFactory
                .selectFrom(member)
                .where(member.email.eq(email))
                .fetchOne());
    }

    @Transactional
    public Address save(Address address) {
        em.persist(address);

        return address;
    }

    public Optional<Address> findAddressById(Long id , Long memberId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(address)
                .where(address.id.eq(id)
                        .and(address.member.id.eq(memberId)))
                .fetchOne());
    }

}
