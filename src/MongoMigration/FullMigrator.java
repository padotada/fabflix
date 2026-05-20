package MongoMigration;

public class FullMigrator {

    public static void main(String[] args) {
        GenresMigrator genresMigrator = new GenresMigrator();

        genresMigrator.migrate();
    }
}
