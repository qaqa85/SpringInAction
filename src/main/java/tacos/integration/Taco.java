package tacos.integration;

import lombok.Data;

import java.util.List;

@Data
public class Taco {
    private final String name;
    private List<String> ingredients;

    @Override
    public String toString() {
        return "Taco{" +
                "name='" + name + '\'' +
                ", ingredients=" + ingredients +
                '}';
    }
}
