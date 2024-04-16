package Model;

import javafx.scene.image.Image;

public class Slide {
    private final String title; // Titlen på sliden.
    private final String content; // Indholdet/teksten associeret med sliden.
    private final Image image; // Billedet for sliden.

    // Konstruktør som initialiserer en slide med titel, indhold og billedsti.
    public Slide(String title, String content, String imagePath) {
        this.title = title;
        this.content = content;
        // Forsøger at indlæse billedet fra filsystemet.
        this.image = new Image("file:" + imagePath); // Ændret til at indlæse fra filsystemet.
        if (image.isError()) {
            throw new IllegalArgumentException("Cannot load image from path: " + imagePath);
        }
    }

    // Getters for at hente slide-information.
    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Image getImage() {
        return image;
    }
}
