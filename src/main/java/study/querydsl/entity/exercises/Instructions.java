package study.querydsl.entity.exercises;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
public class Instructions {
	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@Lob
	@Column(columnDefinition = "TEXT")
	private String instructions;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "exercise_id")
	private Exercise exercise;

	public static Instructions create(String instructions) {
		return Instructions.builder()
			.instructions(instructions)
			.build();
	}

	public void setExercise(Exercise exercise) {
		this.exercise = exercise;
	}
}
