/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package notekeeper;

/**
 *
 * @author Srabon
 */
public class Note {
    private int id;
    private String title, content, updatedAt;

    public Note(int id, String title, String content, String updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.updatedAt = updatedAt;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getUpdatedAt() { return updatedAt; }
}
