package tacos.integration;

public record Ingredient(String code, String name) {
    @Override
    public String toString() {
        return "Ingredient{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
