package study.querydsl;

import static com.querydsl.jpa.JPAExpressions.select;
import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl.entity.QMember.member;
import static study.querydsl.entity.QTeam.team;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

@SpringBootTest
@Transactional
class QuerydslBasicTest {

	@Autowired EntityManager em;

	JPAQueryFactory queryFactory;

	@BeforeEach
	public void before() {
		queryFactory = new JPAQueryFactory(em);
		Team teamA = new Team("teamA");
		Team teamB = new Team("teamB");
		em.persist(teamA);
		em.persist(teamB);

		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member2", 20, teamA);

		Member member3 = new Member("member3", 30, teamB);
		Member member4 = new Member("member4", 40, teamB);

		em.persist(member1);
		em.persist(member2);
		em.persist(member3);
		em.persist(member4);
	}

	@Test
	void startJPQL() {
		String qlString = "select m from Member m "
						+ "where m.username = :username";

		Member findMember = em.createQuery(qlString, Member.class)
			.setParameter("username", "member1")
			.getSingleResult();

		assertThat(findMember.getUsername()).isEqualTo("member1");
	}

	@Test
	void startQuerydsl() {
		// when
		Member findMember = queryFactory
				.select(member)
				.from(member)
				.where(member.username.eq("member1"))
				.fetchOne();

		// then
		assertThat(findMember.getUsername()).isEqualTo("member1");
	}

	@Test
	void search() {
	    // given
		Member findMember = queryFactory
				.selectFrom(member)
				.where(member.username.eq("member1")
				  .and(member.age.eq(10)))
				.fetchOne();
	    // then
	    assertThat(findMember.getUsername()).isEqualTo("member1");
	}

	@Test
	void searchAndParam() {
		Member findMember = queryFactory
				.selectFrom(member)
				.where(
					member.username.eq("member1"),
					member.age.eq(10)
				)
				.fetchOne();
	    // then

	    assertThat(findMember.getUsername()).isEqualTo("member1");
	}

	@Test
	void resultFetch() {
	    // given
		List<Member> members = queryFactory.selectFrom(member).fetch();
		Member member1 = queryFactory.selectFrom(member).fetchOne();
		Member member2 = queryFactory.selectFrom(member).fetchFirst();
	}

	@Test
	void aggreation() {
	    // given
		List<Tuple> result = queryFactory
			.select(member.count(),
					member.age.sum(),
					member.age.avg(),
					member.age.max(),
					member.age.min())
			.from(member)
			.fetch();

		Tuple tuple = result.get(0);
	    assertThat(tuple.get(member.count())).isEqualTo(4);
	    assertThat(tuple.get(member.age.sum())).isEqualTo(100);
	    assertThat(tuple.get(member.age.avg())).isEqualTo(25);
	    assertThat(tuple.get(member.age.max())).isEqualTo(40);
	    assertThat(tuple.get(member.age.min())).isEqualTo(10);
	}

	/**
	 * 팀의 이름과 각 팀의 평균 연령을 구해라.
	 */
	@Test
	void group() {
	    // given
		List<Tuple> result = queryFactory
			.select(team.name, member.age.avg())
			.from(member)
			.join(member.team, team)
			.groupBy(team.name)
			.fetch();

		Tuple teamA = result.get(0);
		Tuple teamB = result.get(1);

		assertThat(teamA.get(team.name)).isEqualTo("teamA");
		assertThat(teamA.get(member.age.avg())).isEqualTo(15);

		assertThat(teamB.get(team.name)).isEqualTo("teamB");
		assertThat(teamB.get(member.age.avg())).isEqualTo(35);
	}

	@Test
	void join() {
	    // given
		List<Member> result = queryFactory
			.selectFrom(member)
			.join(member.team, team)
			.where(team.name.eq("teamA"))
			.fetch();

		assertThat(result)
			.extracting("username")
			.containsExactly("member1", "member2");
	}

	@Test
	void joinOnFiltering() {
	    // given
		List<Tuple> result = queryFactory
				.select(member, team)
				.from(member)
				.leftJoin(member.team, team).on(team.name.eq("teamA"))
				.fetch();

		for (Tuple tuple : result) {
			System.out.println("tuple = " + tuple);
		}
	}

	@PersistenceUnit
	EntityManagerFactory emf;

	@Test
	void fetchJoinNo() {
	    // given
	    em.flush();
		em.clear();

	    // when
		Member findMember = queryFactory
			.selectFrom(member)
			.where(member.username.eq("member1"))
			.fetchOne();

		boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());

		// then
	    assertThat(loaded).as("페치 조인 미적용").isFalse();

	}

	@Test
	void fetchJoinUse() {
		// given
		em.flush();
		em.clear();

		// when
		Member findMember = queryFactory
			.selectFrom(member)
			.join(member.team, team).fetchJoin()
			.where(member.username.eq("member1"))
			.fetchOne();

		boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());

		// then
		assertThat(loaded).as("페치 조인 적용").isTrue();

	}

	@Test
	void subQuery() {
		QMember memberSub = new QMember("memberSub");
	    // given
		List<Member> result = queryFactory
			.selectFrom(member)
			.where(member.age.eq(
				select(memberSub.age.max()).from(memberSub))
				  ).fetch();

	    // then
	    assertThat(result).extracting("age").containsExactly(40);
	}

	@Test
	void basicCase() {
	    // given
		List<String> result = queryFactory
			.select(member.age
				.when(10).then("열살")
				.when(20).then("스무살")
				.otherwise("기타"))
			.from(member)
			.fetch();

		for (String s : result) {
			System.out.println("s = " + s);
		}
	}

	@Test
	void simpleProjection() {
	    // given
		List<String> result = queryFactory
			.select(member.username)
			.from(member)
			.fetch();

		for (String s : result) {
			System.out.println("s = " + s);
		}
	}

	@Test
	void findDtoByQueryProjection() {
	    // given

	    // when
	    
	
	    // then
//	    assertThat();
	    
	}
}