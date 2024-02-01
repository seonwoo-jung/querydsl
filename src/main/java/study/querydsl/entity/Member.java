package study.querydsl.entity;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter
@NoArgsConstructor(access = PROTECTED)
@ToString(of = {"id", "username", "age"})
public class Member {

	@Id
	@GeneratedValue
	@Column(name = "member_id")
	private Long id;

	private String username;

	private int age;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "team_id") // FK명 설정
	private Team team;

	public Member(String username) {
		this(username,0);
	}

	public Member(String username, int age) {
		this(username, age, null);
	}

	public Member(String username, int age, Team team) {
		this.username = username;
		this.age = age;
		if (team != null) {
			changeTeam(team);
		}
	}

	public void changeTeam(Team team) {
		this.team = team;
		team.getMembers().add(this);
	}
}