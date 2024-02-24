package study.querydsl.entity.exercises;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Builder
@AllArgsConstructor
public class PrimaryMuscles {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	private String primaryMuscles;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "exercise_id")
	private Exercise exercise;

	public static PrimaryMuscles create(String primaryMuscles) {
		return PrimaryMuscles.builder()
			.primaryMuscles(primaryMuscles)
			.build();
	}

	public void setExercise(Exercise exercise) {
		this.exercise = exercise;
	}
}
