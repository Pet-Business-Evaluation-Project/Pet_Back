package dev.wework.pet.FindPassword;


import dev.wework.pet.user.findpassword.dto.Request.UserCheckRequest;
import dev.wework.pet.user.findpassword.dto.Response.UserCheckResponse;
import dev.wework.pet.user.findpassword.service.FindPasswordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FindPasswordTest {

    @Autowired
    private FindPasswordService findPasswordService;

    @Test
    @DisplayName("비밀번호 찾기 테스트")
     void testFindPassword(){
        UserCheckRequest request = new UserCheckRequest("fiver0320", "01063940622", "000622-3******");
        UserCheckResponse response = findPasswordService.ExistUserCheck(request);

        System.out.println(response);
    }
}
