package study.querydsl;

import static com.querydsl.core.types.Projections.constructor;
import static study.querydsl.entity.exercises.QExercise.exercise;
import static study.querydsl.entity.exercises.QImages.images1;
import static study.querydsl.entity.exercises.QInstructions.instructions;
import static study.querydsl.entity.exercises.QPrimaryMuscles.primaryMuscles1;
import static study.querydsl.entity.exercises.QSecondaryMuscles.secondaryMuscles1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.annotations.QueryProjection;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.io.File;
import java.io.IOException;
import java.util.List;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.exercises.Exercise;
import study.querydsl.entity.exercises.Images;
import study.querydsl.entity.exercises.Instructions;
import study.querydsl.entity.exercises.PrimaryMuscles;
import study.querydsl.entity.exercises.SecondaryMuscles;

@SpringBootTest
@Transactional
@Slf4j
public class ExerciseTest {

	@Autowired
	private EntityManager em;

	private JPAQueryFactory queryFactory;

	@BeforeEach
	void setup() {
		queryFactory = new JPAQueryFactory(em);
	}

	@Test
	@Rollback(value = false)
	void start() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		List<ExerciseDTO> exercises = mapper.readValue(new File("json/exercises.json"), mapper.getTypeFactory().constructCollectionType(List.class, ExerciseDTO.class));

		for (ExerciseDTO e : exercises) {
			log.info("exercise => {}", e);

			Exercise entity = Exercise.builder()
				.names(e.getName())
				.forces(e.getForce())
				.levels(e.getLevel())
				.mechanics(e.getMechanic())
				.equipments(e.getEquipment())
				.category(e.getCategory())
				.subId(e.getId())
				.build();
			em.persist(entity);

			List<PrimaryMuscles> list = e.getPrimaryMuscles().stream().map(PrimaryMuscles::create).toList();
			list.forEach(x -> x.setExercise(entity));
			list.forEach(o -> em.persist(o));

			List<SecondaryMuscles> secondaryMuscles = e.getSecondaryMuscles().stream().map(SecondaryMuscles::create).toList();
			secondaryMuscles.forEach(x -> x.setExercise(entity));
			secondaryMuscles.forEach(o -> em.persist(o));

			List<Instructions> instructions = e.getInstructions().stream().map(Instructions::create).toList();
			instructions.forEach(x -> x.setExercise(entity));
			instructions.forEach(o -> em.persist(o));

			List<Images> images = e.getImages().stream().map(Images::create).toList();
			images.forEach(x -> x.setExercise(entity));
			images.forEach(o -> em.persist(o));
		}
	}

	@Test
	void selectQueryDSL() {
	    // given
		List<ExerciseResponse> result = queryFactory
			.select(constructor(ExerciseResponse.class,
					exercise.names,
					exercise.forces,
					exercise.levels,
					exercise.mechanics,
					exercise.equipments,
					exercise.category,
					exercise.id))
			.from(exercise)
			.fetch();

		List<ExerciseResponse.PrimaryMusclesResponse> fetch = queryFactory
			.select(constructor(ExerciseResponse.PrimaryMusclesResponse.class,
				primaryMuscles1.primaryMuscles))
			.from(primaryMuscles1)
			.fetch();

//		fetch.forEach(result::addPrimaryMuscles);

		List<ExerciseResponse.SecondaryMusclesResponse> fetch2 = queryFactory
			.select(constructor(ExerciseResponse.SecondaryMusclesResponse.class,
				secondaryMuscles1))
			.from(secondaryMuscles1)
			.fetch();

		List<ExerciseResponse.InstructionsResponse> fetch3 = queryFactory
			.select(constructor(ExerciseResponse.InstructionsResponse.class,
				instructions))
			.from(instructions)
			.fetch();

		List<ExerciseResponse.ImagesResponse> fetch4 = queryFactory
			.select(constructor(ExerciseResponse.ImagesResponse.class,
				images1))
			.from(images1)
			.fetch();
	}

	@Data
	static class ExerciseResponse {
		private String name;
		private String force;
		private String level;
		private String mechanic;
		private String equipment;
		private List<PrimaryMusclesResponse> primaryMuscles;
		private List<SecondaryMusclesResponse> secondaryMuscles;
		private List<InstructionsResponse> instructions;
		private String category;
		private List<ImagesResponse> images;
		private String id;

		public void addPrimaryMuscles(PrimaryMusclesResponse response) {
			this.primaryMuscles.add(response);
		}

		public void addSecondaryMuscles(SecondaryMusclesResponse response) {
			this.secondaryMuscles.add(response);
		}

		public void addInstructions(InstructionsResponse response) {
			this.instructions.add(response);
		}

		public void addImagesResponse(ImagesResponse response) {
			this.images.add(response);
		}

		@QueryProjection
		public ExerciseResponse(String name, String force, String level, String mechanic,
			String equipment,
			List<PrimaryMusclesResponse> primaryMuscles,
			List<SecondaryMusclesResponse> secondaryMuscles,
			List<InstructionsResponse> instructions, String category, List<ImagesResponse> images,
			String id) {
			this.name = name;
			this.force = force;
			this.level = level;
			this.mechanic = mechanic;
			this.equipment = equipment;
			this.primaryMuscles = primaryMuscles;
			this.secondaryMuscles = secondaryMuscles;
			this.instructions = instructions;
			this.category = category;
			this.images = images;
			this.id = id;
		}

		@Data
		public static class PrimaryMusclesResponse {
			private String primaryMuscles;
		}

		@Data
		public static class SecondaryMusclesResponse {
			private String secondaryMuscles;
		}

		@Data
		public static class InstructionsResponse {
			private String instructions;
		}

		@Data
		public static class ImagesResponse {
			private String images;
		}
	}
}
