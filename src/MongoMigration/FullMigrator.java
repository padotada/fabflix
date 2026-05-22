package MongoMigration;

public class FullMigrator {

    public static void main(String[] args) {
        GenresMigrator genresMigrator = new GenresMigrator();
        CustomersMigrator customersMigrator = new CustomersMigrator();
        genresMigrator.migrate();
        customersMigrator.migrate();
    }
}
