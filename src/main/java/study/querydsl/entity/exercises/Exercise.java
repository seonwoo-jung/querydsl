package study.querydsl.entity.exercises;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
public class Exercise {
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "exercise_id")
	private Long id;

	private String names;
	private String forces;
	private String levels;
	private String mechanics;
	private String equipments;

	@OneToMany(mappedBy = "exercise")
	private List<PrimaryMuscles> primaryMuscles = new ArrayList<>();

	@OneToMany(mappedBy = "exercise")
	private List<SecondaryMuscles> secondaryMuscles = new ArrayList<>();

	@OneToMany(mappedBy = "exercise")
	private List<Instructions> instructions = new ArrayList<>();

	private String category;

	@OneToMany(mappedBy = "exercise")
	private List<Images> images = new ArrayList<>();

	private String subId;

	public void add(PrimaryMuscles primaryMuscles) {
		this.primaryMuscles.add(primaryMuscles);
	}
}
