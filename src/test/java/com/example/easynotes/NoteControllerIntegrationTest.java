package com.example.easynotes;

import com.example.easynotes.model.Note;
import com.example.easynotes.repository.NoteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NoteControllerIntegrationTest extends AbstractSpannerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private NoteRepository noteRepository;

    @Test
    public void testCrudOperations() {
        noteRepository.deleteAll();

        // 1. Create a Note
        Note newNote = new Note();
        newNote.setTitle("Test Title");
        newNote.setContent("Test Content");

        ResponseEntity<Note> createResponse = restTemplate.postForEntity("/api/notes", newNote, Note.class);
        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        Note createdNote = createResponse.getBody();
        assertNotNull(createdNote);
        assertNotNull(createdNote.getId());
        assertEquals("Test Title", createdNote.getTitle());
        assertEquals("Test Content", createdNote.getContent());

        // When running tests with H2 in-memory DB or simple setups sometimes auditing needs explicit setup
        // Let's just retrieve it from the DB directly to see if created at is set
        Note fetchedFromDb = noteRepository.findById(createdNote.getId()).orElse(null);
        assertNotNull(fetchedFromDb);
        assertNotNull(fetchedFromDb.getCreatedAt());
        assertNotNull(fetchedFromDb.getUpdatedAt());

        String noteId = createdNote.getId();

        // 2. Get the Note
        ResponseEntity<Note> getResponse = restTemplate.getForEntity("/api/notes/" + noteId, Note.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        Note fetchedNote = getResponse.getBody();
        assertNotNull(fetchedNote);
        assertEquals(noteId, fetchedNote.getId());

        // 3. Update the Note
        fetchedNote.setTitle("Updated Title");
        fetchedNote.setContent("Updated Content");

        HttpEntity<Note> requestEntity = new HttpEntity<>(fetchedNote);
        ResponseEntity<Note> updateResponse = restTemplate.exchange("/api/notes/" + noteId, HttpMethod.PUT, requestEntity, Note.class);
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        Note updatedNote = updateResponse.getBody();
        assertNotNull(updatedNote);
        assertEquals("Updated Title", updatedNote.getTitle());
        assertEquals("Updated Content", updatedNote.getContent());

        // 4. Get all Notes
        ResponseEntity<List<Note>> getAllResponse = restTemplate.exchange(
                "/api/notes",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Note>>() {}
        );
        assertEquals(HttpStatus.OK, getAllResponse.getStatusCode());
        List<Note> notes = getAllResponse.getBody();
        assertNotNull(notes);
        assertEquals(1, notes.size());

        // 5. Delete the Note
        ResponseEntity<Void> deleteResponse = restTemplate.exchange("/api/notes/" + noteId, HttpMethod.DELETE, null, Void.class);
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());

        // Verify deletion
        ResponseEntity<Note> getDeletedResponse = restTemplate.getForEntity("/api/notes/" + noteId, Note.class);
        assertEquals(HttpStatus.NOT_FOUND, getDeletedResponse.getStatusCode());
    }
}
