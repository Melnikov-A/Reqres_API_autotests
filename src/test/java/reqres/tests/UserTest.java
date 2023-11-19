package reqres.tests;

import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import reqres.models.*;
import reqres.specs.Specs;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static reqres.specs.Specs.reqresRequest;
import static reqres.specs.Specs.responseSpecWithCode400;

@DisplayName("Проверка api запросов сайта https://reqres.in")
public class UserTest {

    @Owner("Anton Melnikov")
    @Feature("Тестирование валидации пользователя при вводе неправильного пароля")
    @Test
    @Tag("account")
    @DisplayName("Проверка валидации пользователя")
    @Severity(SeverityLevel.BLOCKER)
    void unsuccessfulPasswordTest() {
        LoginBodyModel data = new LoginBodyModel();
        data.setEmail("peter@klaven");

        LoginErrorResponseModel responseBody = step("Выполнение запроса на валидацию пользователя", () ->
                given(reqresRequest)
                        .body(data)
                        .when()
                        .post("/login")
                        .then()
                        .spec(responseSpecWithCode400)
                        .extract().as(LoginErrorResponseModel.class));

        step("Проверка ответа, что неверный пароль", () -> {
            assertThat(responseBody.getError()).isEqualTo("Missing password");
        });
    }

    @Owner("Anton Melnikov")
    @Feature("Тестирование запроса на наличие email пользователя")
    @Test
    @Tag("userData")
    @DisplayName("Проверка email пользователя")
    @Severity(SeverityLevel.BLOCKER)
    void singleEmailTest() {
        GetUserDataModel data = step("Создание запроса на получение данных пользователя", () ->
                given(reqresRequest)
                        .when()
                        .get("/users/2")
                        .then()
                        .spec(Specs.responseSpecWithCode200)
                        .extract().as(GetUserDataModel.class));

        step("Проверка ответа Email пользователя", () ->
                assertThat(data.getUser().getEmail()).isEqualTo("janet.weaver@reqres.in"));
    }

    @Owner("Anton Melnikov")
    @Feature("Тестирование запроса на наличие названия цвета в пользовательской базе")
    @Test
    @Tag("userData")
    @DisplayName("Проверка наличия выбранного наименования цвета")
    @Severity(SeverityLevel.CRITICAL)
    void singleColourTest() {
        step("Выполнение запроса на получение информации о цвете", () ->
                given(reqresRequest)
                        .when()
                        .get("/unknown")
                        .then()
                        .spec(Specs.responseSpecWithCode200)
                        .body("data.findAll{it.name =~/./}.name.flatten()",
                                hasItem("aqua sky")));
    }

    @Owner("Anton Melnikov")
    @Feature("Тестирование запроса данных пользователя")
    @Test
    @Tag("userData")
    @DisplayName("Проверка данных пользователя")
    @Severity(SeverityLevel.BLOCKER)
    void singleNameTest() {
        GetUserDataModel data = step("Создание запроса на получение данных пользователя", () ->
                given(reqresRequest)
                        .when()
                        .get("/users/2")
                        .then()
                        .spec(Specs.responseSpecWithCode200)
                        .extract().as(GetUserDataModel.class));

        step("Проверка имени и фамилии пользователя", () -> {
            assertThat(data.getUser().getFirstName()).isEqualTo("Janet");
            assertThat(data.getUser().getLastName()).isEqualTo("Weaver");
        });
    }

        @Owner("Anton Melnikov")
        @Feature("Тестирование запроса при создании пользователя")
        @Test
        @Tag("account")
        @DisplayName("Создание нового пользователя")
        @Severity(SeverityLevel.BLOCKER)
        void createUserTest () {
            CreateBodyModel createBody = new CreateBodyModel();
            createBody.setName("Kurt Cobain");
            createBody.setJob("Rock musician");
            CreateResponseModel createResponse = step("Отправка запроса на создание пользователя", () ->
                    given(reqresRequest)
                            .body(createBody)
                            .when()
                            .post("/users")
                            .then()
                            .spec(Specs.responseSpecWithCode201)
                            .extract().as(CreateResponseModel.class));

            step("Проверка ответа: имя, работа", () -> {
                assertThat(createResponse.getName()).isEqualTo("Kurt Cobain");
                assertThat(createResponse.getJob()).isEqualTo("Rock musician");
            });
        }

        @Owner("Anton Melnikov")
        @Feature("Тестирование запроса, что ресурс отсутсвует")
        @Test
        @Tag("userData")
        @DisplayName("Проверка отсутствия ресурса")
        @Severity(SeverityLevel.BLOCKER)
        void singleResourceNotFoundTest () {
            step("Выполнение запроса на отсутствующий ресурс", () ->
                    given(reqresRequest)
                            .when()
                            .get("/unknown/23")
                            .then()
                            .spec(Specs.responseSpecWithCode404));
        }

        @Owner("Anton Melnikov")
        @Feature("Тестирование запроса на удаление данных")
        @Test
        @Tag("account")
        @DisplayName("Удаление данных пользователя")
        @Severity(SeverityLevel.BLOCKER)
        void deleteUserTest () {
            step("Отправка запроса на удаление пользователя", () ->
                    given(reqresRequest)
                            .when()
                            .delete("/users/2")
                            .then()
                            .spec(Specs.responseSpecWithCode204));
        }

    }
