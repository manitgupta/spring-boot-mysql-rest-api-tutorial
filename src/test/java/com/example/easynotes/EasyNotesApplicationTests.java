package com.example.easynotes;

import com.example.easynotes.model.Note;
import com.example.easynotes.repository.NoteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.SpannerEmulatorContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Testcontainers
public class EasyNotesApplicationTests {

	@Container
	static final SpannerEmulatorContainer spanner = new SpannerEmulatorContainer(DockerImageName.parse("gcr.io/cloud-spanner-emulator/emulator"));

	@DynamicPropertySource
	static void spannerProperties(DynamicPropertyRegistry registry) {
		registry.add("SPANNER_JDBC_URL", () -> "jdbc:cloudspanner://" + spanner.getEmulatorGrpcEndpoint() + "/projects/test-project/instances/test-instance/databases/test-database;autoConfigEmulator=true");
	}

	@Autowired
	NoteRepository noteRepository;

	@Test
	public void contextLoads() {
	}

	@Test
	public void saveAndRetrieveNote() {
		Note note = new Note();
		note.setTitle("Spanner Test");
		note.setContent("Testing Spanner integration with Spring Data JPA.");

		Note savedNote = noteRepository.save(note);
		assertNotNull(savedNote.getId());

		Note retrievedNote = noteRepository.findById(savedNote.getId()).orElse(null);
		assertNotNull(retrievedNote);
		assertEquals("Spanner Test", retrievedNote.getTitle());
		assertEquals("Testing Spanner integration with Spring Data JPA.", retrievedNote.getContent());
	}

}
