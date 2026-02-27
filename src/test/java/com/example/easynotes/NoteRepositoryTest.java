package com.example.easynotes;

import com.example.easynotes.model.Note;
import com.example.easynotes.repository.NoteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Date;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class NoteRepositoryTest {

    @Autowired
    private NoteRepository noteRepository;

    @Test
    public void testCreateNote() {
        Note note = new Note();
        note.setTitle("Test Note");
        note.setContent("This is a test note.");
        note.setCreatedAt(new Date());
        note.setUpdatedAt(new Date());

        Note savedNote = noteRepository.save(note);

        assertThat(savedNote.getId()).isNotNull();
        assertThat(savedNote.getTitle()).isEqualTo("Test Note");
    }
}
