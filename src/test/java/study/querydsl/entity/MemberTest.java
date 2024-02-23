package study.querydsl.entity;

import static study.querydsl.entity.QMember.member;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Slf4j
class MemberTest {

	@Autowired
	private EntityManager em;

	private JPAQueryFactory queryFactory;

	@BeforeEach
	void setup() {
		queryFactory = new JPAQueryFactory(em);
	}

	@Test
	void testEntity() {
	    // given
		Team teamA = new Team("teamA");
		Team teamB = new Team("teamB");
		em.persist(teamA);
		em.persist(teamB);

	    // when
		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member2", 20, teamA);
		Member member3 = new Member("member3", 30, teamB);
		Member member4 = new Member("member4", 40, teamB);

		em.persist(member1);
		em.persist(member2);
		em.persist(member3);
		em.persist(member4);

		em.flush();
		em.clear();

		List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();

		for (Member member : members) {
			System.out.println("member = " + member);
			System.out.println("member.getTeam() = " + member.getTeam());
		}
	}

	@Test
	@DisplayName("조건절 동적쿼리 작성")
	void queryDSL() {
		Member entity = Member.builder()
			.username("seonwoo_jung")
			.age(27)
			.build();
		em.persist(entity);

		List<Member> members = queryFactory.select(member)
			.from(member)
			.where(eqUsername(null), eqAge(null))
			.fetch();

		for (Member member : members) {
			log.info("member => {}", member);
		}
	}

	private BooleanExpression eqAge(Integer age) {
		if (Objects.isNull(age)) {
			return null;
		}
		return member.age.eq(age);
	}

	private BooleanExpression eqUsername(String username) {
		if (StringUtils.isBlank(username)) {
			return null;
		}
		return member.username.eq(username);
	}
}