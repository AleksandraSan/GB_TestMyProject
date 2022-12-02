package gb.ru;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AbstractTest {
    static Properties prop = new Properties();// класс проперти
    private static InputStream configFile;
    private static String baseUrl;
    private static String username;
    private static String password;
    private static String token;
    private static String owner;
    private static String sort;

    protected static ResponseSpecification responseSpecification;
    protected static RequestSpecification requestSpecification;


    @BeforeAll
    static void initTest() throws IOException {
        configFile = new FileInputStream("src/main/resources/my.properties");
        prop.load(configFile);
        baseUrl= prop.getProperty("base_url");
        username = prop.getProperty("username");
        password = prop.getProperty("password");
        token = prop.getProperty("token");
        owner = prop.getProperty("owner");
        sort = prop.getProperty("sort");

        requestSpecification = new RequestSpecBuilder()
                //.addHeader("X-Auth-Token", getToken())
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();

        responseSpecification = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectStatusLine("HTTP/1.1 200 OK")
                .expectContentType(ContentType.JSON)
                .expectResponseTime(Matchers.lessThan(6000L))
                .build();

        RestAssured.responseSpecification = responseSpecification;
        RestAssured.requestSpecification = requestSpecification;
    }

    @BeforeAll
    static void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    public static String getPassword() {
        return password;
    }

    public static String getOwner() {
        return owner;
    }

    public static String getSort() {
        return sort;
    }

    public static ResponseSpecification getResponseSpecification() {
        return responseSpecification;
    }

    public static RequestSpecification getRequestSpecification() {
        return requestSpecification;
    }

    public static String getUsername() {
        return username;
    }

    public static String getToken(){
        return token;
    }

    public static String getBaseUrl() {return baseUrl;}

}



