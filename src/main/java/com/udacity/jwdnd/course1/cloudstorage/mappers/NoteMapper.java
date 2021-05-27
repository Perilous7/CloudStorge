package com.udacity.jwdnd.course1.cloudstorage.mappers;

import com.udacity.jwdnd.course1.cloudstorage.models.Note;
import com.udacity.jwdnd.course1.cloudstorage.models.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NoteMapper {
    @Select("SELECT * FROM NOTES WHERE userId= #{userId}")
    List<Note> getAllNotes(int userId);

    @Select("SELECT * FROM NOTES WHERE noteId = #{noteId}")
    Note getNote(int noteId);

    @Insert("INSERT INTO NOTES (notetitle, notedescription, userId) VALUES(#{notetitle}, #{notedescription}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "noteId")
    int addNote(Note note);

    @Update("UPDATE FILES SET userId = #{userId}, notetitle = #{notetitle} WHERE noteId = #{noteId}")
    int updateNote(int noteId, String notetitle, String description);

    @Delete("DELETE FROM FILES WHERE noteId = #{noteId}")
    int deleteNote(int noteId);


}
