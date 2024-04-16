package Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Presentation {
    private final ObservableList<Slide> slides = FXCollections.observableArrayList();

    public ObservableList<Slide> getSlides() { return slides; }
}
