package gb.ru;

import gb.Datum;
import gb.Meta;
import gb.ResponsePost;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import jdk.jfr.Name;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.proxy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class StandTest extends AbstractTest {

    @Name("Тест1:Авторизация c валидным логином и паролем")
    @Test
    void getTokenTest() {
         given()
                .body("{\n"
                        + " \"username\":" + getUsername() + ",\n"
                        + " \"password\":" + getPassword() + ",\n"
                        + "}")
                .when()
                .post("https://test-stand.gb.ru/gateway/login")
                .then()
                .spec(getResponseSpecification());
    }

    @Name("Авторизация невалидным логином")
    @Test
    void invalidAuthorizationTest() {
        given()
                .body("{\n"
                        + " \"username\":Im,\n"
                        + " \"password\":d0fa7c3b59 ,\n"
                        + "}")
                .when()
                .post("https://test-stand.gb.ru/gateway/login")
                .then()
                .statusCode(400);
    }


    @Name("Тест1:Получение списка СВОИХ постов с параметром сортировки по возрастанию")
    @Test
    void getMyPostTest() {
        given()
                .queryParam("order", "ASC")
                .spec(getRequestSpecification())
                .header("X-Auth-Token", getToken())
                .when()
                .get("https://test-stand.gb.ru/api/posts")
                .then()
                .spec(getResponseSpecification());
    }

    @Name("Тест2:Получение списка СВОИХ ПОСТОВ с параметром сортировки по убыванию")
    @Test
    void getMyPostWithDescTest() {
        given()
                .queryParam("order", "DESC")
                .spec(getRequestSpecification())
                .header("X-Auth-Token", getToken())
                .when()
                .get("https://test-stand.gb.ru/api/posts")
                .then()
                .spec(getResponseSpecification());
    }

    @Name("Тест3:Получение списка СВОИХ ПОСТОВ не авторизованным пользователем")
    @Test
    void getMyPostUnauthorizedUserTest() {
        given()
                .when()
                .get("https://test-stand.gb.ru/api/posts")
                .then()
                .assertThat()
                .statusCode(401)
                .statusLine("HTTP/1.1 401 Unauthorized");
    }

    @Name("Тест4: Получение списка постов с несуществующим номером страницы")
    @Test
    void getMyPostPageTest() {
        ResponsePost responsePost = given()
                .header("X-Auth-Token", getToken())
                .queryParam("page", 1212983)
                .spec(getRequestSpecification())
                .when()
                .get("https://test-stand.gb.ru/api/posts")
                .then()
                .extract()
                .response()
                .body()
                .as(ResponsePost.class);

        /* При попытке запроса СВОИХ постов с параметром несуществующей страницы
        возвращается пустой массив data(объекты постов с информацией о них).
         */

        assertThat(responsePost.getData().size(), equalTo(0));
    }

    @Name("Тест5:Получение списка постов с комбинацией всех query параметров")
    @Test
    void getMyPostQueryParamTest() {
        given()
                .header("X-Auth-Token", getToken())
                .queryParam("page", 13)
                .queryParam("sort", getSort())
                .queryParam("order", "ASC")
                .spec(getRequestSpecification())
                .when()
                .get("https://test-stand.gb.ru/api/posts")
                .then()
                .spec(getResponseSpecification());
    }

    @Name("Тест6: Количество своих постов на странице должно быть 10")
    @Test
    void getMyPostCountTest() {
        ResponsePost response = given()
                .spec(getRequestSpecification())
                .header("X-Auth-Token", getToken())
                .when()
                .get("https://test-stand.gb.ru/api/posts")
                .then()
                .extract()
                .response()
                .body()
                .as(ResponsePost.class);

        assertThat(response.getMeta().getCount(), equalTo(10));
    }


    @Name("Тест1:Получение ЧУЖИХ постов на определенной странице с сортировкой по возрастанию")
    @Test
    void getNotMePostTest() {
       ResponsePost response =  given()
                .header("X-Auth-Token", getToken())
                .spec(getRequestSpecification())
                .queryParam("owner", getOwner())
                .queryParam("order", "ASC")
                .queryParam("page" , 1)
                .when()
                .get("https://test-stand.gb.ru/api/posts")
                .then()
                .extract()
                .response()
                .body()
                .as(ResponsePost.class);
    }

    @Name("Тест2:Получение чужих постов с параметром ALL для рандомного показа всех постов")
    @Test
    void getNotMeAllPostTest() {
        given()
                .header("X-Auth-Token", getToken())
                .spec(getRequestSpecification())
                .queryParam("owner", getOwner())
                .queryParam("order", "ALL")
                .when()
                .get("https://test-stand.gb.ru/api/posts")
                .then()
                .spec(getResponseSpecification());
    }

    @Name("Тест3:Получение ЧУЖИХ постов неавторизованным пользователем")
    @Test
    void getNotMePostUnauthorizedTest() {
        given()
                .queryParam("owner", getOwner())
                .when()
                .get("https://test-stand.gb.ru/api/posts")
                .then()
                .assertThat()
                .statusCode(401)
                .statusLine("HTTP/1.1 401 Unauthorized");
    }

    @Name("Тест4:Получение списка ЧУЖИХ ПОСТОВ с параметром сортировки по убыванию")
    @Test
    void getNotMePostWithDescTest() {
        given()
                .queryParam("order", "DESC")
                .queryParam("owner", getOwner())
                .spec(getRequestSpecification())
                .header("X-Auth-Token", getToken())
                .when()
                .get("https://test-stand.gb.ru/api/posts")
                .then()
                .spec(getResponseSpecification());
    }

    @Name("Тест5: Получение списка ЧУЖИХ постов с несуществующим номером страницы")
    @Test
    void getNotMyPostPageTest() {
        ResponsePost response = given()
                .header("X-Auth-Token", getToken())
                .queryParam("owner", getOwner())
                .queryParam("page", "7897089")
                .spec(getRequestSpecification())
                .when()
                .get("https://test-stand.gb.ru/api/posts")
                .then()
                .extract()
                .response()
                .body()
                .as(ResponsePost.class);

        /* При попытке запросить посты с несуществующим номером страницы
        возвращается пустой массив data(объекты постов с информацией)*/

        assertThat(response.getData().size(), equalTo(0));
    }

    @Name("Тест6:Количество ЧУЖИХ постов на одной странице должно быть не более 4")
    @Test
    void getCountNotMePostTest() {
        ResponsePost responsePost = given()
                .header("X-Auth-Token", getToken())
                .queryParam("owner", getOwner())
                .spec(getRequestSpecification())
                .when()
                .get("https://test-stand.gb.ru/api/posts")
                .then()
                .extract()
                .response()
                .body()
                .as(ResponsePost.class);

        /* Атрибут дата содержит в себе объект поста с информацией
        (таким образом мы сможем посчитать количесто объектом (постов) на странице)
        Исходя из требований постов на одной странице может быть не более 4.
         */
        assertThat(responsePost.getData().size(), lessThan(5));
    }
}