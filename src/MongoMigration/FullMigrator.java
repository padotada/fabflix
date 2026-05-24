package MongoMigration;

public class FullMigrator {

    public static void main(String[] args) {
        GenresMigrator genresMigrator = new GenresMigrator();
        genresMigrator.migrate();
//
//        CustomersMigrator customersMigrator = new CustomersMigrator();
//        customersMigrator.migrate();
//
//        EmployeesMigrator employeesMigrator = new EmployeesMigrator();
//        employeesMigrator.migrate();

//        MoviesMigrator moviesMigrator = new MoviesMigrator();
//        moviesMigrator.migrate();

//        StarsMigrator starsMigrator = new StarsMigrator();
//        starsMigrator.migrate();
//
//        SalesMigrator salesMigrator = new SalesMigrator();
//        salesMigrator.migrate();
    }
}
