# CODING\_STANDARDS 鈥?缂栫爜瑙勮寖鎵嬪唽

> 鏈枃妗ｆ槸 ADR-001 鐨勫疄鏂界粏鑺傝ˉ鍏咃紝渚涗汉闃呰锛堝挨鍏舵槸鏂版垚鍛?onboarding锛夈€侫I 缂栫▼鎸囦护瑙侀」鐩牴鐩綍 
>
> `.cursorrules`
>
> 銆?


***

## 涓€銆佸悗绔紪鐮佽鑼?
### 1.1 鍖呬笌鍛藉悕



| 瑙勮寖椤?          | 瑕佹眰                                                                        |
| ------------- | ------------------------------------------------------------------------- |
| 鏍瑰寘            | `com.by.microservices.{妯″潡鍚峿`                                              |
| Entity        | `domain/entity/` 涓嬶紝绫诲悕棣栧瓧姣嶅ぇ鍐欙紙濡?`User.java`锛?                                |
| Repository 鎺ュ彛 | `domain/repository/` 涓嬶紝绾帴鍙ｏ紙鏃犱换浣曟鏋舵敞瑙ｏ級锛屽懡鍚嶅悗缂€ `Repository`                     |
| DTO           | `application/dto/` 涓嬶紝鎸夌敤閫旀坊鍔犲悗缂€锛歚Command`锛堝懡浠わ級銆乣Query`锛堟煡璇級銆乣Response`锛堝搷搴旓級     |
| Controller    | `interfaces/rest/` 涓嬶紝鍛藉悕鍚庣紑 `Controller`                                    |
| 搴旂敤鏈嶅姟          | `application/service/` 涓嬶紝鍛藉悕鍚庣紑 `ApplicationService`                        |
| 鐢ㄤ緥瀹炵幇          | `application/usecase/` 涓嬶紝鍛藉悕鍓嶇紑 `{涓氬姟鍔ㄤ綔} + UseCase`锛堝 `RegisterUserUseCase`锛?|

### 1.2 DTO 瑙勮寖



* 浼樺厛浣跨敤 Java 16+ `record`锛堜笉鍙彉瀵硅薄锛夛紝澶嶆潅鍦烘櫙杈呬互 Lombok `@Getter @Builder`锛堢姝?`@Data` 閬垮厤 Setter锛?
* 蹇呴』娣诲姞 JSR-303 鏍￠獙娉ㄨВ锛坄@NotBlank`銆乣@NotNull`銆乣@Email`銆乣@Size` 绛夛級锛屾槑纭瓧娈电害鏉?
* 绂佹浣跨敤 `Map`銆乣JSONObject` 鎴栨棤绫诲瀷瀵硅薄鎺ユ敹 Controller 鍙傛暟

* 瀛楁鍛藉悕閲囩敤灏忛┘宄帮紙涓?JSON 搴忓垪鍖栦繚鎸佷竴鑷达級



```
// 鉁?姝ｇ‘锛坮ecord 鏂瑰紡锛屾帹鑽愶級

public record RegisterUserCommand(

&#x20;   @NotBlank(message = "閭涓嶈兘涓虹┖")

&#x20;   @Email(message = "閭鏍煎紡涓嶆纭?)

&#x20;   String email,

&#x20;   @NotBlank(message = "瀵嗙爜涓嶈兘涓虹┖")

&#x20;   @Size(min = 8, max = 32, message = "瀵嗙爜闀垮害蹇呴』鍦?8-32 浣嶄箣闂?)

&#x20;   String password,

&#x20;   @NotBlank(message = "鐢ㄦ埛鍚嶄笉鑳戒负绌?)

&#x20;   @Size(min = 2, max = 20, message = "鐢ㄦ埛鍚嶉暱搴﹀繀椤诲湪 2-20 浣嶄箣闂?)

&#x20;   String username

) {}

// 鉁?姝ｇ‘锛堝鏉傚満鏅?Lombok 鏂瑰紡锛?
@Getter

@Builder

public class UpdateUserInfoCommand {

&#x20;   @NotNull(message = "鐢ㄦ埛ID涓嶈兘涓虹┖")

&#x20;   private final Long userId;

&#x20;   @Size(max = 20, message = "鏄电О闀垮害涓嶈兘瓒呰繃 20 浣?)

&#x20;   private final String nickname;

&#x20;   @Pattern(regexp = "^1\[3-9]\\\d{9}\$", message = "鎵嬫満鍙锋牸寮忎笉姝ｇ‘")

&#x20;   private final String phone;

}

// 鉂?閿欒锛堟棤绫诲瀷绾︽潫锛?
public void register(@RequestBody Map> request) { ... }

// 鉂?閿欒锛堢己灏戞牎楠屾敞瑙ｏ級

public record LoginCommand(String username, String password) {}
```

### 1.3 鍝嶅簲鏍煎紡



* 鍏ㄥ眬缁熶竴鍝嶅簲鍖呰绫?\`ApiResponse 缁撴瀯鍥哄畾锛?


```
{

&#x20; "code": 200,    // 鐘舵€佺爜锛?00 鎴愬姛锛岄潪 200 澶辫触锛?
&#x20; "message": "OK",// 鎻愮ず淇℃伅锛堝け璐ユ椂杩斿洖鍏蜂綋鍘熷洜锛?
&#x20; "data": {}      // 鍝嶅簲鏁版嵁锛堟垚鍔熸椂杩斿洖锛屽け璐ユ椂鍙负 null锛?
}
```



* 閿欒鐮佺粺涓€浣跨敤鍏ㄥ眬鏋氫妇 `ErrorCode`锛岀姝㈢‖缂栫爜鏁板瓧

* 閫氳繃 `GlobalExceptionHandler` 缁熶竴鎹曡幏寮傚父骞惰繑鍥炴爣鍑嗘牸寮忥紝绂佹 Controller 涓墜鍔ㄦ崟鑾峰悗鑷畾涔夎繑鍥?


```
// 鍏ㄥ眬鍝嶅簲绫荤ず渚?
@Getter

public class ApiResponse> {

&#x20;   private final int code;

&#x20;   private final String message;

&#x20;   private final T data;

&#x20;   // 鎴愬姛鍝嶅簲锛堝甫鏁版嵁锛?
&#x20;   public static \<T> success(T data) {

&#x20;       return new ApiResponse.getCode(), ErrorCode.SUCCESS.getMessage(), data);

&#x20;   }

&#x20;   // 鎴愬姛鍝嶅簲锛堟棤鏁版嵁锛?
&#x20;   public static \<T> ApiResponse success() {

&#x20;       return success(null);

&#x20;   }

&#x20;   // 澶辫触鍝嶅簲

&#x20;   public static > ApiResponse(ErrorCode errorCode) {

&#x20;       return new ApiResponse<>(errorCode.getCode(), errorCode.getMessage(), null);

&#x20;   }

}

// 鍏ㄥ眬寮傚父澶勭悊鍣ㄧず渚?
@RestControllerAdvice

public class GlobalExceptionHandler {

&#x20;   // 鍙傛暟鏍￠獙寮傚父

&#x20;   @ExceptionHandler(MethodArgumentNotValidException.class)

&#x20;   public ApiResponseException(MethodArgumentNotValidException e) {

&#x20;       String message = e.getBindingResult().getFieldErrors().stream()

&#x20;               .map(FieldError::getDefaultMessage)

&#x20;               .collect(Collectors.joining("锛?));

&#x20;       return ApiResponse.fail(ErrorCode.PARAM\_VALIDATION\_FAILED.setMessage(message));

&#x20;   }

&#x20;   // 涓氬姟寮傚父

&#x20;   @ExceptionHandler(BusinessException.class)

&#x20;   public ApiResponse handleBusinessException(BusinessException e) {

&#x20;       return ApiResponse.fail(e.getErrorCode());

&#x20;   }

}
```

### 1.4 瀵硅薄鏄犲皠



* 蹇呴』浣跨敤 MapStruct 杩涜瀵硅薄杞崲锛圗ntity 鈫?DTO銆乂O 绛夛級锛岀紪璇戞湡鐢熸垚浠ｇ爜锛岄浂杩愯鏃跺紑閿€

* 绂佹浣跨敤 `BeanUtils.copyProperties`锛堟祬鎷疯礉銆佹棤绫诲瀷鏍￠獙銆佹€ц兘宸級

* MapStruct 鎺ュ彛鏀惧湪 `infrastructure/mapper/struct/` 鐩綍涓嬶紝鍛藉悕鍚庣紑 `Mapper`



```
// MapStruct 杞崲鎺ュ彛绀轰緥

@Mapper(componentModel = "spring")

public interface UserStructMapper {

&#x20;   UserStructMapper INSTANCE = Mappers.getMapper(UserStructMapper.class);

&#x20;   // Entity 鈫?Response DTO

&#x20;   UserResponse toResponse(User user);

&#x20;   // Command 鈫?Entity

&#x20;   @Mapping(target = "id", ignore = true) // ID 鑷锛屽拷鐣ヨ祴鍊?
&#x20;   @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")

&#x20;   @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")

&#x20;   @Mapping(target = "deleted", constant = "false")

&#x20;   User toEntity(RegisterUserCommand command);

}
```

### 1.5 鏂规硶闀垮害涓庡鏉傚害



| 鏂规硶绫诲瀷                      | 鏈€澶ц鏁?| 澶囨敞                       |
| ------------------------- | ---- | ------------------------ |
| 鏅€氫笟鍔℃柟娉曪紙Service / UseCase锛?| 50 琛?| 瓒呭嚭鍒欐媶鍒嗕负绉佹湁鏂规硶鎴栫嫭绔嬬被           |
| 宸ュ叿绫绘柟娉?                    | 80 琛?| 鍏佽閫傚綋鏀惧锛屼絾闇€鍔犺缁嗘敞閲?          |
| Controller 鏂规硶             | 20 琛?| 浠呭仛鍙傛暟鎺ユ敹銆佹潈闄愭牎楠屻€佽皟鐢ㄦ湇鍔★紝涓嶅寘鍚笟鍔￠€昏緫 |
| MyBatis Mapper 鎺ュ彛鏂规硶       | 1 琛? | 浠呭畾涔夋柟娉曠鍚嶏紝SQL 鍐欏湪 XML 涓?    |



* 鏂规硶鍙傛暟涓暟涓嶈秴杩?5 涓紝瓒呭嚭鍒欏皝瑁呬负 DTO

* 绂佹宓屽瓒呰繃 3 灞傛潯浠跺垽鏂紙if/else銆乫or 寰幆锛夛紝閫氳繃鎻愬墠杩斿洖銆佺瓥鐣ユā寮忎紭鍖?
### 1.6 娉ㄩ噴瑙勮寖



* **绫绘敞閲?*锛氭墍鏈?public 绫诲繀椤绘坊鍔?Javadoc 娉ㄩ噴锛岃鏄庣被鑱岃矗銆佷綔鑰呫€佸垱寤烘棩鏈?
* **鏂规硶娉ㄩ噴**锛氭墍鏈?public 鏂规硶蹇呴』娣诲姞 Javadoc 娉ㄩ噴锛岃鏄庡姛鑳姐€佸弬鏁板惈涔夈€佽繑鍥炲€笺€佹姏鍑哄紓甯?
* **瀛楁娉ㄩ噴**锛欴TO 鏍￠獙娉ㄨВ鐨?`message` 宸茶鏄庣害鏉熺殑锛屾棤闇€棰濆娉ㄩ噴锛涘鏉傚瓧娈碉紙濡傜姸鎬佺爜锛夐渶琛ュ厖璇存槑

* **澶嶆潅閫昏緫娉ㄩ噴**锛氱鏈夋柟娉曟垨澶嶆潅涓氬姟閫昏緫锛堝绠楁硶銆佸鏉′欢鍒ゆ柇锛夐渶娣诲姞琛屾敞閲婏紝璇存槑璁捐鎰忓浘

* **绂佹浜嬮」**锛氱姝㈢暀涓?`TODO`锛堣浆涓?GitHub Issue 骞舵爣娉ㄩ摼鎺ワ級銆乣FIXME` 绛変复鏃舵敞閲?


```
/\*\*

&#x20;\* 鐢ㄦ埛娉ㄥ唽鐢ㄤ緥瀹炵幇

&#x20;\* 璐熻矗澶勭悊鐢ㄦ埛娉ㄥ唽鐨勬牳蹇冧笟鍔￠€昏緫锛氬弬鏁版牎楠屻€佸瘑鐮佸姞瀵嗐€佹暟鎹叆搴撱€佸彂閫佹敞鍐屼簨浠?
&#x20;\*

&#x20;\* @author 寮€鍙戣€呭鍚?
&#x20;\* @date 2026-04-29

&#x20;\*/

@Service

public class RegisterUserUseCase {

&#x20;   private final UserRepository userRepository;

&#x20;   private final PasswordEncoder passwordEncoder;

&#x20;   private final EventPublisher eventPublisher;

&#x20;   /\*\*

&#x20;    \* 鎵ц鐢ㄦ埛娉ㄥ唽

&#x20;    \*

&#x20;    \* @param command 娉ㄥ唽鍛戒护锛堝寘鍚偖绠便€佸瘑鐮併€佺敤鎴峰悕锛?
&#x20;    \* @return 娉ㄥ唽鎴愬姛鐨勭敤鎴稩D

&#x20;    \* @throws BusinessException 褰撻偖绠卞凡琚敞鍐屾椂鎶涘嚭

&#x20;    \*/

&#x20;   public Long execute(RegisterUserCommand command) {

&#x20;       // 1. 鏍￠獙閭鏄惁宸叉敞鍐岋紙涓氬姟绾︽潫锛氶偖绠卞敮涓€锛?
&#x20;       if (userRepository.existsByEmail(command.email())) {

&#x20;           throw new BusinessException(ErrorCode.EMAIL\_ALREADY\_REGISTERED);

&#x20;       }

&#x20;       // 2. 瀵嗙爜鍔犲瘑锛圔Crypt 鍔犵洂鍝堝笇锛?
&#x20;       String encryptedPassword = passwordEncoder.encode(command.password());

&#x20;       // 3. 杞崲涓?Entity 骞朵繚瀛?
&#x20;       User user = UserStructMapper.INSTANCE.toEntity(command);

&#x20;       user.setPassword(encryptedPassword);

&#x20;       User savedUser = userRepository.save(user);

&#x20;       // 4. 鍙戦€佹敞鍐屾垚鍔熶簨浠讹紙寮傛閫氱煡鍏朵粬鏈嶅姟锛?
&#x20;       eventPublisher.publish(new UserRegisteredEvent(savedUser.getId(), savedUser.getEmail()));

&#x20;       return savedUser.getId();

&#x20;   }

}
```

### 1.7 绂佹浜嬮」



* 绂佹浣跨敤 `System.out.println`銆乣e.printStackTrace()`锛堢粺涓€浣跨敤 Logback 鏃ュ織妗嗘灦锛?
* 绂佹纭紪鐮侀瓟娉曟暟瀛椼€佸瓧绗︿覆锛堟娊鍙栦负甯搁噺鎴栨灇涓撅紝甯搁噺绫绘斁鍦?`domain/constant/` 涓嬶級

* 绂佹鎶涘嚭鏈崟鑾风殑 `RuntimeException`锛堣嚜瀹氫箟涓氬姟寮傚父 `BusinessException` 缁熶竴澶勭悊锛?
* MyBatis 涓姝娇鐢?`${...}` 鎷兼帴 SQL锛堜粎鐢?`#{...}` 棰勭紪璇戯紝闃叉 SQL 娉ㄥ叆锛?
* 绂佹鍦?`domain` 灞傚紩鍏ヤ换浣曟鏋朵緷璧栵紙濡?Spring `@Component`銆丮yBatis `@Mapper` 绛夛級

* 绂佹浣跨敤 `static` 闈欐€佸彉閲忓瓨鍌ㄤ笟鍔＄姸鎬侊紙鏄撳紩鍙戝苟鍙戦棶棰橈級

* 绂佹鍦?Controller 涓紪鍐欎笟鍔￠€昏緫锛堜粎鍋氳姹傞€傞厤鍜屽搷搴斿寘瑁咃級

### 1.8 MyBatis 瑙勮寖



* Mapper 鎺ュ彛鏀惧湪 `infrastructure/persistence/` 涓嬶紝鍛藉悕鍚庣紑 `Mapper`

* Mapper XML 鏂囦欢鏀惧湪 `src/main/resources/mapper/` 涓嬶紝涓?Mapper 鎺ュ彛鍚屽悕锛岀洰褰曠粨鏋勪竴鑷?
* SQL 璇彞蹇呴』娣诲姞娉ㄩ噴锛岃鏄庡姛鑳藉拰鍙傛暟鍚箟

* 鍔ㄦ€?SQL 浼樺厛浣跨敤 `<if>`銆乣<foreach>`锛岀姝㈠瓧绗︿覆鎷兼帴

* 鏌ヨ缁撴灉蹇呴』鏄犲皠鍒板疄浣撶被鎴?DTO锛岀姝㈣繑鍥?\`List\<Map

* 鎵归噺鎿嶄綔浣跨敤 \` 閬垮厤寰幆璋冪敤鍗曟潯 SQL

* 鍒嗛〉鏌ヨ蹇呴』浣跨敤 `PageHelper` 鎴?MyBatis-Plus 鍒嗛〉鎻掍欢锛岀姝㈡墜鍐?`LIMIT ? OFFSET ?`



```
\<!-- 鉁?姝ｇ‘绀轰緥 -->

1.0" encoding="UTF-8"?>

\<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"&#x20;

"http://mybatis.org/dtd/mybatis-3-mapper.dtd">

\="com.by.microservices.user.infrastructure.persistence.UserMapper">

&#x20;   鍒楄〃锛堝垎椤碉級 -->

&#x20;   UserList" resultType="com.by.microservices.user.application.dto.UserResponse">

&#x20;       SELECT

&#x20;           id AS userId,

&#x20;           username,

&#x20;           email,

&#x20;           nickname,

&#x20;           created\_at AS createdAt

&#x20;       FROM tb\_user

&#x20;       \>

&#x20;           deleted = 0

&#x20;           \="username != null and username != ''">

&#x20;               AND username LIKE CONCAT('%', #{username}, '%')

&#x20;             null and email != ''">

&#x20;               AND email = #{email}

&#x20;           \>

&#x20;      &#x20;

&#x20;       ORDER BY created\_at DESC

&#x20;  &#x20;
```



***

## 浜屻€佸墠绔紪鐮佽鑼?
### 2.1 API 璋冪敤瑙勮寖



* API 鎺ュ彛缁熶竴鏀惧湪 `src/api/` 鐩綍涓嬶紝鎸変笟鍔℃ā鍧楀垝鍒嗘枃浠讹紙濡?`user.ts`銆乣order.ts`锛?
* 鎵€鏈?API 鍑芥暟杩斿洖 `PromiseResponse浠?`@microservices/types\` 瀵煎叆锛堢姝㈡墜鍔ㄥ畾涔夛級

* 缁熶竴澶勭悊璇锋眰鎷︽埅鍣紙娣诲姞 Token锛夈€佸搷搴旀嫤鎴櫒锛堢粺涓€閿欒澶勭悊锛?
* 鍑芥暟鍛藉悕閲囩敤灏忛┘宄帮紝鍓嶇紑鏄庣‘涓氬姟鍔ㄤ綔锛坄get`/`create`/`update`/`delete`锛?


```
// 鉁?姝ｇ‘绀轰緥

import type { User, UserQueryParams, PageResponse } from '@microservices/types';

import { request } from '@/utils/request'; // 灏佽鍚庣殑璇锋眰宸ュ叿锛堝惈鎷︽埅鍣級

/\*\*

&#x20;\* 鏌ヨ鐢ㄦ埛鍒楄〃锛堝垎椤碉級

&#x20;\* @param params 鏌ヨ鍙傛暟

&#x20;\*/

export const getUserList = async (

&#x20; params: UserQueryParams

): Promise\<ApiResponse\<PageResponse

&#x20; return request({

&#x20;   url: '/api/users',

&#x20;   method: 'GET',

&#x20;   params,

&#x20; });

};

/\*\*

&#x20;\* 鍒涘缓鐢ㄦ埛

&#x20;\* @param data 鐢ㄦ埛鍒涘缓鍙傛暟

&#x20;\*/

export const createUser = async (data: Omitid'>): PromiseResponse  return request({

&#x20;   url: '/api/users',

&#x20;   method: 'POST',

&#x20;   data,

&#x20; });

};
```

### 2.2 鐩綍缁撴瀯瑙勮寖锛坅dmin /web 閫氱敤锛?


```
src/

鈹溾攢鈹€ api/           # API 璋冪敤灞傦紙鎸夋ā鍧楀垝鍒嗭級

鈹溾攢鈹€ views/         # 椤甸潰缁勪欢锛堣矾鐢卞搴旈〉闈級

鈹?  鈹溾攢鈹€ user/      # 涓氬姟妯″潡鐩綍

鈹?  鈹?  鈹溾攢鈹€ UserList.vue  # 鍒楄〃椤?
鈹?  鈹?  鈹溾攢鈹€ UserDetail.vue # 璇︽儏椤?
鈹?  鈹?  鈹斺攢鈹€ UserForm.vue  # 琛ㄥ崟椤?
鈹溾攢鈹€ components/     # 鍏叡缁勪欢锛堝叏灞€澶嶇敤锛?
鈹?  鈹溾攢鈹€ common/     # 閫氱敤缁勪欢锛堟寜閽€佽緭鍏ユ绛夛級

鈹?  鈹斺攢鈹€ business/   # 涓氬姟缁勪欢锛堢敤鎴峰崱鐗囥€佽鍗曡〃鏍肩瓑锛?
鈹溾攢鈹€ stores/        # 鐘舵€佺鐞嗭紙Pinia锛?
鈹?  鈹溾攢鈹€ userStore.ts # 鐢ㄦ埛鐩稿叧鐘舵€?
鈹?  鈹斺攢鈹€ appStore.ts  # 搴旂敤鍏ㄥ眬鐘舵€?
鈹溾攢鈹€ router/        # 璺敱閰嶇疆锛堟寜妯″潡鎷嗗垎锛?
鈹?  鈹溾攢鈹€ index.ts    # 璺敱鍏ュ彛

鈹?  鈹斺攢鈹€ modules/    # 妯″潡璺敱

鈹?      鈹溾攢鈹€ userRouter.ts

鈹?      鈹斺攢鈹€ orderRouter.ts

鈹溾攢鈹€ utils/         # 宸ュ叿鍑芥暟锛堟牸寮忓寲銆佹牎楠岀瓑锛?
鈹溾攢鈹€ styles/        # 鍏ㄥ眬鏍峰紡锛堜富棰樸€侀噸缃牱寮忕瓑锛?
鈹斺攢鈹€ types/         # 鏈湴绫诲瀷琛ュ厖锛堝叡浜被鍨嬩紭鍏堜粠 packages/types 瀵煎叆锛?```

### 2.3 TypeScript 瑙勮寖



* 绂佹浣跨敤 `any` 绫诲瀷锛堟湭鐭ョ被鍨嬬敤 `unknown` + 绫诲瀷瀹堝崼锛屼复鏃跺吋瀹圭敤 `// @ts-ignore` 骞舵爣娉ㄥ師鍥狅級

* 鎺ュ彛 / 绫诲瀷瀹氫箟浼樺厛浣跨敤 `interface`锛堝彲鎵╁睍锛夛紝绠€鍗曠被鍨嬪埆鍚嶇敤 `type`

* 鍏变韩绫诲瀷蹇呴』浠?`@microservices/types` 瀵煎叆锛岀姝㈡墜鍔ㄥ鍒舵垨閲嶅瀹氫箟

* 缁勪欢 Props 蹇呴』閫氳繃 `defineProps` 瀹氫箟骞舵寚瀹氱被鍨嬶紝绂佹鏃犵被鍨嬩紶閫?
* 浜嬩欢閫氳繃 `defineEmits` 澹版槑锛屾槑纭弬鏁扮被鍨?


```
ts">

// 鉁?姝ｇ‘绀轰緥锛堢粍浠?Props 涓?Emits锛?
import type { User } from '@microservices/types';

const props = defineProps: User;

&#x20; isEditable: boolean;

}>();

const emit = defineEmits (e: 'edit', userId: number): void;

&#x20; (e: 'delete', userId: number): void;

}>();

// 鉁?姝ｇ‘绀轰緥锛堢被鍨嬪畧鍗級

function formatUser(user: unknown): string {

&#x20; if (typeof user !== 'object' || user === null) {

&#x20;   return '鏈煡鐢ㄦ埛';

&#x20; }

&#x20; const userObj = user as Partial return userObj.username || userObj.email || '鏈煡鐢ㄦ埛';

}

// 鉂?閿欒绀轰緥锛堢姝?any锛?
function handleUserData(data: any) {

&#x20; console.log(data.username);

}
```

### 2.4 缁勪欢瑙勮寖



* 浼樺厛浣跨敤缁勫悎寮?API锛坄setup` 璇硶绯栵級锛岀姝㈤€夐」寮?API

* 缁勪欢鍛藉悕閲囩敤 PascalCase锛堝 `UserForm.vue`锛夛紝涓?Vue 瀹樻柟鎺ㄨ崘涓€鑷?
* 鍏叡缁勪欢娉ㄥ唽鍒板叏灞€锛屼笟鍔＄粍浠跺眬閮ㄥ鍏?
* 缁勪欢鍐呴儴閫昏緫鎷嗗垎鍒?`composables/` 鐩綍锛堝 `useUserForm.ts`锛夛紝淇濇寔缁勪欢绠€娲?
* 绂佹鍦ㄦā鏉夸腑缂栧啓澶嶆潅琛ㄨ揪寮忥紝鎻愬彇涓鸿绠楀睘鎬ф垨鏂规硶



```
\>

&#x20; \="user-card">

&#x20;   -title">{{ user.username }}2>

&#x20;    class="card-content">

&#x20;     {{ user.email }}

&#x20;     鏃堕棿锛歿{ formatDateTime(user.createdAt) }}

&#x20;        v-if="isEditable"

&#x20;     type="primary"

&#x20;     @click="emit('edit', user.id)"

&#x20;   \>

&#x20;     缂栬緫

&#x20;   \>

&#x20;&#x20;

\</template>

&#x20;setup lang="ts">

import { formatDateTime } from '@/utils/date';

import type { User } from '@microservices/types';

const props = defineProps: User;

&#x20; isEditable: boolean;

}>();

const emit = defineEmits (e: 'edit', userId: number): void;

}>();

\>

/\* 灞€閮ㄦ牱寮忥紝閬垮厤姹℃煋鍏ㄥ眬 \*/

.card-title {

&#x20; font-size: 18px;

&#x20; margin-bottom: 16px;

}

\---

\## 涓夈€佹暟鎹簱瑙勮寖

\### 3.1 Flyway 杩佺Щ瑙勮寖

\- 鑴氭湰鍛藉悕鏍煎紡锛歕`V{鐗堟湰鍙穧\_\_{鍔熻兘鎻忚堪}.sql\`锛堢増鏈彿閫掑锛屽弻涓嬪垝绾垮垎闅旓級锛岀ず渚嬶細\`V1\_\_init\_user\_table.sql\`銆乗`V2\_\_add\_user\_nickname\_column.sql\`

\- 姣忎釜鏈嶅姟鐙珛缁存姢杩佺Щ鑴氭湰锛屾斁鍦?\`src/main/resources/db/migration/\` 鐩綍涓?
\- 杩佺Щ鑴氭湰蹇呴』鏄箓绛夌殑锛堝娆℃墽琛屾棤鍓綔鐢級锛屾柊澧炲瓧娈甸渶鎸囧畾榛樿鍊?
\- 绂佹淇敼宸叉彁浜ゅ埌 Git 浠撳簱涓斿凡鍦ㄧ敓浜х幆澧冩墽琛岀殑杩佺Щ鑴氭湰锛堝闇€淇敼锛屾柊澧炶縼绉昏剼鏈級

\- 鑴氭湰涓繀椤绘坊鍔犳敞閲婏紝璇存槑杩佺Щ鐩殑鍜屽彉鏇村唴瀹?
\`\`\`sql

\-- V2\_\_add\_user\_nickname\_column.sql

\-- 涓虹敤鎴疯〃娣诲姞鏄电О瀛楁锛堥粯璁ょ┖瀛楃涓诧紝闈炲繀濉級

ALTER TABLE tb\_user

ADD COLUMN nickname VARCHAR(20) NOT NULL DEFAULT '' COMMENT '鐢ㄦ埛鏄电О' AFTER username;

\-- 涓烘樀绉版坊鍔犵储寮曪紙浼樺寲鏌ヨ锛?
CREATE INDEX idx\_tb\_user\_nickname ON tb\_user(nickname);
```

### 3.2 琛ㄨ璁¤鑼?


| 瑙勮寖椤? | 瑕佹眰                                                                               | 绀轰緥                                                                                                                                                                          |
| ---- | -------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 琛ㄥ悕   | 鍏ㄥ皬鍐欙紝涓嬪垝绾垮垎闅旓紝鍓嶇紑 `tb_` + 涓氬姟妯″潡鍚?                                                      | `tb_user`锛堢敤鎴疯〃锛夈€乣tb_user_role`锛堢敤鎴疯鑹插叧鑱旇〃锛?                                                                                                                                     |
| 瀛楁鍚? | 鍏ㄥ皬鍐欙紝涓嬪垝绾垮垎闅旓紝璇箟鏄庣‘                                                                   | `user_id`锛堢敤鎴?ID锛夈€乣login_time`锛堢櫥褰曟椂闂达級                                                                                                                                         |
| 涓婚敭   | 缁熶竴鍛藉悕 `id`锛岀被鍨?`BIGINT AUTO_INCREMENT`锛堣嚜澧炰富閿級                                       | `id BIGINT AUTO_INCREMENT PRIMARY KEY`                                                                                                                                      |
| 鏃堕棿鎴? | 蹇呴』鍖呭惈 `created_at`锛堝垱寤烘椂闂达級銆乣updated_at`锛堟洿鏂版椂闂达級锛岀被鍨?`DATETIME`锛岄粯璁ゅ€?`CURRENT_TIMESTAMP` | `created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿'`銆乣updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿'` |
| 杞垹闄? | 缁熶竴浣跨敤 `deleted` 瀛楁锛岀被鍨?`TINYINT`锛? = 鏈垹闄わ紝1 = 宸插垹闄わ級锛岄粯璁ゅ€?0                            | `deleted TINYINT NOT NULL DEFAULT 0 COMMENT '鏄惁鍒犻櫎锛?=鍚︼紝1=鏄級'`                                                                                                                |
| 瀛楁绫诲瀷 | 鎸変笟鍔″満鏅€夋嫨鏈€灏忓彲琛岀被鍨嬶紙濡傛墜鏈哄彿鐢?`CHAR(11)` 鑰岄潪 `VARCHAR(20)`锛?                                | 鐢ㄦ埛鍚嶏細`VARCHAR(20)`銆佸瘑鐮佸搱甯岋細`CHAR(60)`锛圔Crypt 鍔犲瘑鍚庡浐瀹氶暱搴︼級                                                                                                                           |
| 娉ㄩ噴   | 琛ㄥ拰瀛楁蹇呴』娣诲姞 `COMMENT` 娉ㄩ噴锛岃鏄庣敤閫?                                                      | `COMMENT '鐢ㄦ埛琛紙瀛樺偍鐢ㄦ埛鍩虹淇℃伅锛?`                                                                                                                                                   |
| 绱㈠紩   | 鎸夋煡璇㈤鐜囨坊鍔犵储寮曪紝閬垮厤鍏ㄨ〃鎵弿锛涜仈鍚堢储寮曢伒寰€屾渶宸﹀墠缂€鍘熷垯銆?                                                 | 鐧诲綍鏌ヨ锛歚INDEX idx_tb_user_email (email)`锛涘垪琛ㄧ瓫閫夛細`INDEX idx_tb_user_status_create_time (status, created_at)`                                                                     |

### 3.3 SQL 缂栧啓瑙勮寖



* 鍏抽敭瀛楀ぇ鍐欙紙`SELECT`銆乣FROM`銆乣WHERE`銆乣JOIN` 绛夛級锛岃〃鍚嶅拰瀛楁鍚嶅皬鍐欙紝鍖哄垎澶у皬鍐欐彁鍗囧彲璇绘€?
* 澶氳〃鍏宠仈鏌ヨ蹇呴』浣跨敤琛ㄥ埆鍚嶏紝閬垮厤瀛楁姝т箟

* 绂佹 `SELECT *`锛屾槑纭寚瀹氶渶瑕佹煡璇㈢殑瀛楁锛堝噺灏戞暟鎹紶杈撱€侀伩鍏嶅瓧娈靛彉鏇村奖鍝嶏級

* `WHERE` 鏉′欢涓紭鍏堜娇鐢ㄧ储寮曞瓧娈碉紝閬垮厤浣跨敤 `!=`銆乣NOT IN`銆乣IS NULL` 绛夊鑷寸储寮曞け鏁堢殑鎿嶄綔

* 鍒嗛〉鏌ヨ蹇呴』鎸囧畾 `ORDER BY`锛堥伩鍏嶅垎椤电粨鏋滀笉涓€鑷达級锛屼笖鎺掑簭瀛楁闇€娣诲姞绱㈠紩



```
\-- 鉁?姝ｇ‘绀轰緥

SELECT

&#x20; u.id AS user\_id,

&#x20; u.username,

&#x20; u.email,

&#x20; r.name AS role\_name

FROM tb\_user u

LEFT JOIN tb\_user\_role ur ON u.id = ur.user\_id

LEFT JOIN tb\_role r ON ur.role\_id = r.id

WHERE

&#x20; u.deleted = 0

&#x20; AND u.status = 1

&#x20; AND u.created\_at >= '2026-01-01'

ORDER BY u.created\_at DESC

LIMIT 10 OFFSET 20;

\-- 鉂?閿欒绀轰緥锛圫ELECT \* + 鏃?ORDER BY + 绱㈠紩澶辨晥锛?
SELECT \* FROM tb\_user WHERE status != 0 LIMIT 10 OFFSET 20;
```



***

## 鍥涖€丏ockerfile 瑙勮寖

### 4.1 閫氱敤瑕佹眰



* 鎵€鏈夋湇鍔＄殑 Dockerfile 鏀惧湪 `deployments/` 鐩綍涓嬶紙濡?`apps/user-service/deployments/Dockerfile`锛?
* 閲囩敤澶氶樁娈垫瀯寤猴紙Build Stage + Runtime Stage锛夛紝鍑忓皬闀滃儚浣撶Н

* 鍩虹闀滃儚浼樺厛閫夋嫨 Alpine 鐗堟湰锛堣交閲忥級锛孞ava 鏈嶅姟浣跨敤 `eclipse-temurin:21-jre-alpine`锛堜粎鍚?JRE锛屼笉鍚?JDK锛?
* 瀹瑰櫒鍐呯姝娇鐢?root 鐢ㄦ埛杩愯鏈嶅姟锛屽垱寤轰笓鐢ㄩ潪 root 鐢ㄦ埛

* 鏆撮湶鏈嶅姟绔彛锛坄EXPOSE` 鎸囦护锛夛紝娉ㄦ槑绔彛鐢ㄩ€?
### 4.2 鍚庣鏈嶅姟 Dockerfile 绀轰緥



```
\# 绗竴闃舵锛氭瀯寤猴紙Maven 鏋勫缓鐜锛?
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

\# 璁剧疆宸ヤ綔鐩綍

WORKDIR /app

\# 澶嶅埗 pom.xml 鍜屼緷璧栨枃浠讹紝缂撳瓨渚濊禆锛堝姞閫熸瀯寤猴級

COPY pom.xml .

COPY src ./src

\# 鏋勫缓 Jar 鍖咃紙璺宠繃娴嬭瘯锛岀敓浜ф瀯寤洪渶绉婚櫎 -DskipTests锛?
RUN mvn clean package -DskipTests -U

\# 绗簩闃舵锛氳繍琛岋紙浠呭惈 JRE锛屽噺灏忛暅鍍忎綋绉級

FROM eclipse-temurin:21-jre-alpine

\# 鍒涘缓闈?root 鐢ㄦ埛锛堝畨鍏ㄦ渶浣冲疄璺碉級

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

\# 璁剧疆宸ヤ綔鐩綍

WORKDIR /app

\# 浠庢瀯寤洪樁娈靛鍒?Jar 鍖?
COPY --from=builder /app/target/\*.jar app.jar

\# 鎺堟潈缁欓潪 root 鐢ㄦ埛

RUN chown -R appuser:appgroup /app

\# 鍒囨崲鐢ㄦ埛

USER appuser

\# 鏆撮湶鏈嶅姟绔彛锛堜笌 application.yml 涓€鑷达級

EXPOSE 8081

\# 鍚姩鍛戒护锛堟寚瀹氱幆澧冨彉閲忥紝鏀寔閰嶇疆瑕嗙洊锛?
ENTRYPOINT \["java", "-jar", "app.jar", "--spring.profiles.active=\${SPRING\_PROFILES\_ACTIVE:dev}"]
```

### 4.3 鍓嶇鏈嶅姟 Dockerfile 绀轰緥



```
\# 绗竴闃舵锛氭瀯寤猴紙Node 鏋勫缓鐜锛?
FROM node:22.11.0-alpine AS builder

\# 璁剧疆宸ヤ綔鐩綍

WORKDIR /app

\# 澶嶅埗 package.json 鍜?pnpm-lock.yaml

COPY package.json pnpm-lock.yaml ./

\# 瀹夎 pnpm

RUN npm install -g pnpm

\# 瀹夎渚濊禆锛堢紦瀛樹緷璧栵級

RUN pnpm install

\# 澶嶅埗婧愪唬鐮?
COPY . .

\# 鏋勫缓鐢熶骇鐗堟湰

RUN pnpm build

\# 绗簩闃舵锛氳繍琛岋紙Nginx 闈欐€佹湇鍔″櫒锛?
FROM nginx:alpine

\# 浠庢瀯寤洪樁娈靛鍒舵瀯寤轰骇鐗╁埌 Nginx 闈欐€佺洰褰?
COPY --from=builder /app/dist /usr/share/nginx/html

\# 澶嶅埗 Nginx 閰嶇疆鏂囦欢锛堝闇€鑷畾涔夌鍙ｃ€佸弽鍚戜唬鐞嗙瓑锛?
COPY deployments/nginx.conf /etc/nginx/conf.d/default.conf

\# 鏆撮湶绔彛

EXPOSE 80

\# 鍚姩 Nginx

CMD \["nginx", "-g", "daemon off;"]
```



***

## 浜斻€佸彲瑙傛祴瑙勮寖

### 5.1 鏃ュ織瑙勮寖



* 鏃ュ織妗嗘灦锛歀ogback + Logstash Encoder锛岃緭鍑?JSON 鏍煎紡鏃ュ織锛堜究浜?Loki 鏀堕泦锛?
* 鏃ュ織绾у埆锛歚ERROR`锛堥敊璇級銆乣WARN`锛堣鍛婏級銆乣INFO`锛堥噸瑕佷俊鎭級銆乣DEBUG`锛堣皟璇曚俊鎭級锛岀敓浜х幆澧冪鐢?`DEBUG`

* 蹇呴』鍖呭惈瀛楁锛歚traceId`锛堥摼璺拷韪?ID锛夈€乣spanId`锛堣法搴?ID锛夈€乣timestamp`锛堟椂闂存埑锛夈€乣level`锛堟棩蹇楃骇鍒級銆乣logger`锛堟棩蹇楀櫒鍚嶏級銆乣message`锛堟棩蹇椾俊鎭級銆乣serviceName`锛堟湇鍔″悕锛?
* 鏁忔劅淇℃伅鑴辨晱锛氬瘑鐮併€乀oken銆佹墜鏈哄彿銆佽韩浠借瘉鍙风瓑鏁忔劅淇℃伅蹇呴』鑴辨晱鍚庤緭鍑猴紙濡傛墜鏈哄彿鏄剧ず涓?`138****1234`锛?
* 鏃ュ織杈撳嚭浣嶇疆锛氭爣鍑嗚緭鍑猴紙STDOUT锛夛紝绂佹鍐欏叆鏈湴鏂囦欢锛堝鍣ㄥ寲閮ㄧ讲鏃ュ織閫氳繃 Docker 鏀堕泦锛?


```
閰嶇疆绀轰緥 -->

\>

&#x20;    name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">

&#x20;       \="net.logstash.logback.encoder.LogstashEncoder">

&#x20;           Fields>{"serviceName":"user-service"}>

&#x20;           \>

&#x20;               timestamp \<level>level>

&#x20;               logger \<message>message>

&#x20;               \>traceId>

&#x20;               \>spanId>

&#x20;           \>

&#x20;      &#x20;

&#x20;  &#x20;

&#x20;   INFO">

&#x20;       ender-ref ref="CONSOLE" />

&#x20;   \</root>

&#x20;   妗嗘灦鏃ュ織绾у埆璋冩暣 -->

&#x20;   .springframework" level="WARN" />

&#x20;   \="com.by.microservices" level="INFO" />
```

### 5.2 閾捐矾杩借釜瑙勮寖



* 浣跨敤 OpenTelemetry 鑷姩鍩嬬偣锛屾棤闇€鎵嬪姩璋冪敤 `Tracer.getCurrentSpan()`

* 鎵€鏈夊井鏈嶅姟銆佺綉鍏炽€佹秷鎭槦鍒楀繀椤绘帴鍏ラ摼璺拷韪紝纭繚 `traceId` 璺ㄦ湇鍔°€佽法绾跨▼銆佽法娑堟伅浼犻€?
* 鍏抽敭涓氬姟娴佺▼锛堝鐢ㄦ埛娉ㄥ唽銆佽鍗曞垱寤猴級闇€娣诲姞鑷畾涔夎法搴︼紙Span锛夛紝鏍囨敞涓氬姟鍔ㄤ綔

* 閾捐矾杩借釜鏁版嵁瀵煎嚭鍒?Jaeger锛岄€氳繃 Jaeger UI 鏌ョ湅鍏ㄩ摼璺€楁椂鍜岃皟鐢ㄥ叧绯?


```
// 鑷畾涔夐摼璺法搴︾ず渚嬶紙鍏抽敭涓氬姟娴佺▼锛?
@Service

public class OrderCreateUseCase {

&#x20;   private final Tracer tracer;

&#x20;   public Long execute(CreateOrderCommand command) {

&#x20;       // 鍒涘缓鑷畾涔夎法搴︼紝鏍囨敞涓氬姟鍔ㄤ綔

&#x20;       Span span = tracer.spanBuilder("order-create-usecase")

&#x20;               .setAttribute("order.userId", command.userId())

&#x20;               .setAttribute("order.productCount", command.products().size())

&#x20;               .startSpan();

&#x20;       try (Scope scope = span.makeCurrent()) {

&#x20;           // 涓氬姟閫昏緫...

&#x20;           return orderId;

&#x20;       } catch (Exception e) {

&#x20;           span.recordException(e);

&#x20;           throw e;

&#x20;       } finally {

&#x20;           span.end();

&#x20;       }

&#x20;   }

}
```



***

## 鍏€丟it 瑙勮寖

### 6.1 鍒嗘敮绛栫暐



* `main`锛氫富鍒嗘敮锛岀敓浜х幆澧冧唬鐮侊紝绂佹鐩存帴鎻愪氦

* `develop`锛氬紑鍙戝垎鏀紝闆嗘垚娴嬭瘯閫氳繃鍚庡悎骞跺埌 `main`

* `feature/{鍔熻兘鍚峿`锛氬姛鑳藉垎鏀紝浠?`develop` 鍒嗘敮鍒涘缓锛屽畬鎴愬悗鍚堝苟鍥?`develop`锛堝 `feature/user-register`锛?
* `bugfix/{闂鎻忚堪}`锛歜ug 淇鍒嗘敮锛屼粠 `develop` 鍒嗘敮鍒涘缓锛屼慨澶嶅悗鍚堝苟鍥?`develop`锛堝 `bugfix/email-validation`锛?
* `hotfix/{闂鎻忚堪}`锛氱揣鎬ヤ慨澶嶅垎鏀紝浠?`main` 鍒嗘敮鍒涘缓锛屼慨澶嶅悗鍚屾椂鍚堝苟鍒?`main` 鍜?`develop`锛堝 `hotfix/login-failure`锛?
### 6.2 Commit 瑙勮寖



* 鏍煎紡锛歚{type}: {subject}`锛堢被鍨嬶細绠€鐭弿杩帮級锛岀ず渚嬶細`feat: 瀹炵幇鐢ㄦ埛娉ㄥ唽鍔熻兘`銆乣fix: 淇閭鏍煎紡鏍￠獙bug`

* **Type 绫诲瀷**锛?

  * `feat`锛氭柊鍔熻兘

  * `fix`锛歜ug 淇

  * `docs`锛氭枃妗ｆ洿鏂帮紙濡?README銆佺紪鐮佽鑼冿級

  * `style`锛氫唬鐮佹牸寮忚皟鏁达紙涓嶅奖鍝嶅姛鑳斤紝濡傜缉杩涖€佺┖鏍硷級

  * `refactor`锛氫唬鐮侀噸鏋勶紙涓嶅奖鍝嶅姛鑳斤紝濡傛柟娉曟媶鍒嗐€佸彉閲忛噸鍛藉悕锛?
  * `test`锛氭坊鍔犳垨淇敼娴嬭瘯鐢ㄤ緥

  * `chore`锛氭瀯寤鸿剼鏈€佷緷璧栨洿鏂扮瓑鏉傞」

* **Subject 鎻忚堪**锛?

  * 棣栧瓧姣嶅皬鍐欙紝缁撳熬涓嶅姞鏍囩偣

  * 绠€娲佹槑浜嗭紙涓嶈秴杩?50 瀛楃锛夛紝璇存槑銆屽仛浜嗕粈涔堛€嶈€岄潪銆屾€庝箞鍋氥€?
### 6.3 鎻愪氦绾︽潫



* 绂佹鎻愪氦 `.env` 鏂囦欢銆両DE 閰嶇疆鏂囦欢锛堝 `.idea`銆乣.vscode`锛夈€佹瀯寤轰骇鐗╋紙濡?`target`銆乣dist`锛?
* 绂佹鍦?commit message 涓寘鍚瘑鐮併€乀oken銆佸瘑閽ョ瓑鏁忔劅淇℃伅

* 姣忔鎻愪氦鍙寘鍚竴涓姛鑳芥垨涓€涓?bug 淇锛岄伩鍏嶅ぇ鏉傜儵鎻愪氦

* 鎻愪氦鍓嶅繀椤昏繍琛屾湰鍦版祴璇曞拰 Lint锛岀‘淇濅唬鐮佺鍚堣鑼?
### 6.4 PR/MR 瑙勮寖



* 鍔熻兘鍒嗘敮瀹屾垚鍚庯紝閫氳繃 Pull Request/Merge Request 鍚堝苟鍒扮洰鏍囧垎鏀?
* PR/MR 鏍囬鏍煎紡涓?Commit 涓€鑷达紙`{type}: {subject}`锛?
* PR/MR 鎻忚堪闇€璇存槑鍔熻兘鐐广€佹祴璇曞満鏅€佸奖鍝嶈寖鍥?
* 鑷冲皯闇€瑕?1 鍚嶅洟闃熸垚鍛?Code Review 閫氳繃鍚庢墠鑳藉悎骞?
* 鍚堝苟鍓嶅繀椤婚€氳繃 CI 娴佹按绾匡紙娴嬭瘯銆丩int銆佹瀯寤猴級



***

## 涓冦€佹祴璇曡鑼?
### 7.1 娴嬭瘯瑕嗙洊鐜囪姹?


| 浠ｇ爜灞傜骇                          | 鏈€浣庤鐩栫巼 | 娴嬭瘯绫诲瀷                            |
| ----------------------------- | ----- | ------------------------------- |
| Controller                    | 80%   | 鍗曞厓娴嬭瘯锛圡ock 鏈嶅姟灞傦級                  |
| Application Service / UseCase | 90%   | 鍗曞厓娴嬭瘯锛圡ock Repository锛?          |
| Repository                    | 95%   | 闆嗘垚娴嬭瘯锛圱estcontainers + 鐪熷疄 MySQL锛?|
| 宸ュ叿绫?                          | 95%   | 鍗曞厓娴嬭瘯锛堣鐩栨墍鏈夊垎鏀級                    |

### 7.2 娴嬭瘯鍦烘櫙瑕嗙洊

姣忎釜鎺ュ彛 / 鏂规硶蹇呴』瑕嗙洊浠ヤ笅鍦烘櫙锛?


* 姝ｅ父涓氬姟娴佺▼锛堝弬鏁板悎娉曘€侀€昏緫姝ｇ‘锛?
* 鍙傛暟鏍￠獙澶辫触锛堝繀濉」涓虹┖銆佹牸寮忛敊璇€侀暱搴﹁秴鍑洪檺鍒讹級

* 鏉冮檺涓嶈冻 / 鏈璇侊紙濡傛湭鐧诲綍璁块棶闇€鎺堟潈鎺ュ彛锛?
* 璧勬簮涓嶅瓨鍦紙濡傛煡璇笉瀛樺湪鐨勭敤鎴?ID锛?
* 涓氬姟寮傚父锛堝浣欓涓嶈冻銆佸簱瀛樹笉澶燂級

* 骞跺彂鍦烘櫙锛堝绉掓潃銆佸苟鍙戞洿鏂板悓涓€璧勬簮锛?
### 7.3 娴嬭瘯瑙勮寖



* 浣跨敤 JUnit 5 + Mockito 杩涜鍗曞厓娴嬭瘯锛孴estcontainers 杩涜闆嗘垚娴嬭瘯

* 娴嬭瘯绫诲懡鍚嶏細`{琚祴璇曠被鍚峿Test`锛堝 `UserServiceTest`锛?
* 娴嬭瘯鏂规硶鍛藉悕锛歚{娴嬭瘯鍦烘櫙} + Should + {棰勬湡缁撴灉}`锛堝 `registerWithValidParamShouldReturnUserId`锛?
* 绂佹浣跨敤 `@Disabled` 璺宠繃娴嬭瘯锛堥櫎闈炴湁鐗规畩鍘熷洜骞舵爣娉ㄨ鏄庯級

* 绂佹鍦ㄦ祴璇曚腑浣跨敤 `System.out` 鏇夸唬鏂█锛堜娇鐢?AssertJ 鏂█搴擄紝璇箟鏇存竻鏅帮級

* 娴嬭瘯鏁版嵁浣跨敤闅忔満鐢熸垚锛堝 `RandomStringUtils`锛夛紝绂佹纭紪鐮佸浐瀹氭暟鎹?


```
// 鉁?姝ｇ‘绀轰緥锛堝崟鍏冩祴璇曪級

@ExtendWith(MockitoExtension.class)

public class RegisterUserUseCaseTest {

&#x20;   @Mock

&#x20;   private UserRepository userRepository;

&#x20;   @Mock

&#x20;   private PasswordEncoder passwordEncoder;

&#x20;   @InjectMocks

&#x20;   private RegisterUserUseCase registerUserUseCase;

&#x20;   @Test

&#x20;   void registerWithValidParamShouldReturnUserId() {

&#x20;       // 1. 鍑嗗娴嬭瘯鏁版嵁

&#x20;       RegisterUserCommand command = new RegisterUserCommand(

&#x20;           "test@example.com",

&#x20;           "Password123",

&#x20;           "testuser"

&#x20;       );

&#x20;       Long expectedUserId = 1L;

&#x20;       User mockUser = User.builder()

&#x20;           .id(expectedUserId)

&#x20;           .email(command.email())

&#x20;           .username(command.username())

&#x20;           .password("encryptedPassword")

&#x20;           .build();

&#x20;       // 2. Mock 渚濊禆琛屼负

&#x20;       when(userRepository.existsByEmail(command.email())).thenReturn(false);

&#x20;       when(passwordEncoder.encode(command.password())).thenReturn("encryptedPassword");

&#x20;       when(userRepository.save(any(User.class))).thenReturn(mockUser);

&#x20;       // 3. 鎵ц娴嬭瘯

&#x20;       Long actualUserId = registerUserUseCase.execute(command);

&#x20;       // 4. 鏂█缁撴灉

&#x20;       assertThat(actualUserId).isEqualTo(expectedUserId);

&#x20;       verify(userRepository).existsByEmail(command.email());

&#x20;       verify(passwordEncoder).encode(command.password());

&#x20;       verify(userRepository).save(any(User.class));

&#x20;   }

&#x20;   @Test

&#x20;   void registerWithDuplicateEmailShouldThrowException() {

&#x20;       // 1. 鍑嗗娴嬭瘯鏁版嵁

&#x20;       RegisterUserCommand command = new RegisterUserCommand(

&#x20;           "duplicate@example.com",

&#x20;           "Password123",

&#x20;           "testuser"

&#x20;       );

&#x20;       // 2. Mock 渚濊禆琛屼负锛堥偖绠卞凡瀛樺湪锛?
&#x20;       when(userRepository.existsByEmail(command.email())).thenReturn(true);

&#x20;       // 3. 鎵ц娴嬭瘯骞舵柇瑷€寮傚父

&#x20;       BusinessException exception = assertThrows(BusinessException.class, () -> {

&#x20;           registerUserUseCase.execute(command);

&#x20;       });

&#x20;       assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.EMAIL\_ALREADY\_REGISTERED);

&#x20;       verify(userRepository).existsByEmail(command.email());

&#x20;       verifyNoMoreInteractions(passwordEncoder, userRepository);

&#x20;   }

}
```



***

## 鍏€佸叚杈瑰舰鏋舵瀯瀹炴柦缁嗗垯

### 8.1 鍒嗗眰渚濊禆瑙勫垯



* 渚濊禆鏂瑰悜锛歚interfaces` 鈫?`application` 鈫?`domain` 鈫?`infrastructure`锛堜弗鏍煎崟鍚戜緷璧栵級

* `domain` 灞傦細鏍稿績灞傦紝闆朵緷璧栵紙涓嶄緷璧栦换浣曟鏋躲€佸叾浠栧眰浠ｇ爜锛夛紝浠呭寘鍚函 Java 浠ｇ爜锛圥OJO銆佹帴鍙ｃ€佹灇涓撅級

* `application` 灞傦細渚濊禆 `domain` 灞傦紝涓嶄緷璧?`infrastructure` 鍜?`interfaces` 灞傦紝瀹氫箟涓氬姟鐢ㄤ緥鍜?DTO

* `infrastructure` 灞傦細渚濊禆 `domain` 灞傦紝瀹炵幇 `domain` 灞傚畾涔夌殑鎺ュ彛锛堝 Repository銆丒ventPublisher锛夛紝鍖呭惈妗嗘灦渚濊禆鍜屽閮ㄨ祫婧愯闂?
* `interfaces` 灞傦細渚濊禆 `application` 灞傦紝璐熻矗鎺ユ敹澶栭儴璇锋眰锛圚TTP銆丮Q锛夊苟杞彂缁欏簲鐢ㄦ湇鍔★紝涓嶅寘鍚笟鍔￠€昏緫

### 8.2 鍖呯粨鏋勫畬鏁寸ず渚嬶紙user-service锛?


```
com.by.microservices.user/

鈹溾攢鈹€ UserServiceApplication.java  # 搴旂敤鍏ュ彛锛堜粎閰嶇疆鎵弿锛屾棤涓氬姟閫昏緫锛?
鈹?
鈹溾攢鈹€ domain/                      # 棰嗗煙灞傦紙闆朵緷璧栵級

鈹?  鈹溾攢鈹€ entity/

鈹?  鈹?  鈹斺攢鈹€ User.java            # 棰嗗煙瀹炰綋锛堢函 POJO锛孈Getter @Builder锛?
鈹?  鈹溾攢鈹€ vo/

鈹?  鈹?  鈹斺攢鈹€ Email.java           # 鍊煎璞★紙灏佽閭鏍￠獙閫昏緫锛?
鈹?  鈹溾攢鈹€ event/

鈹?  鈹?  鈹溾攢鈹€ UserRegisteredEvent.java  # 棰嗗煙浜嬩欢

鈹?  鈹?  鈹斺攢鈹€ EventPublisher.java       # 浜嬩欢鍙戝竷鎺ュ彛锛堝畾涔夊湪 domain锛?
鈹?  鈹溾攢鈹€ repository/

鈹?  鈹?  鈹斺攢鈹€ UserRepository.java       # 浠撳偍鎺ュ彛锛堝畾涔夊湪 domain锛?
鈹?  鈹斺攢鈹€ constant/

鈹?      鈹斺攢鈹€ UserStatus.java           # 棰嗗煙甯搁噺锛堟灇涓撅級

鈹?
鈹溾攢鈹€ application/                 # 搴旂敤灞傦紙渚濊禆 domain锛?
鈹?  鈹溾攢鈹€ service/

鈹?  鈹?  鈹斺攢鈹€ UserApplicationService.java  # 搴旂敤鏈嶅姟鎺ュ彛

鈹?  鈹溾攢鈹€ usecase/

鈹?  鈹?  鈹溾攢鈹€ RegisterUserUseCase.java     # 娉ㄥ唽鐢ㄤ緥瀹炵幇

鈹?  鈹?  鈹斺攢鈹€ QueryUserUseCase.java        # 鏌ヨ鐢ㄤ緥瀹炵幇

鈹?  鈹斺攢鈹€ dto/

鈹?      鈹溾攢鈹€ RegisterUserCommand.java     # 鍛戒护 DTO

鈹?      鈹溾攢鈹€ QueryUserQuery.java          # 鏌ヨ DTO

鈹?      鈹斺攢鈹€ UserResponse.java            # 鍝嶅簲 DTO

鈹?
鈹溾攢鈹€ infrastructure/              # 鍩虹璁炬柦灞傦紙渚濊禆 domain锛屽疄鐜板叾鎺ュ彛锛?
鈹?  鈹溾攢鈹€ persistence/

鈹?  鈹?  鈹溾攢鈹€ UserMapper.java              # MyBatis Mapper 鎺ュ彛

鈹?  鈹?  鈹斺攢鈹€ UserRepositoryImpl.java      # 浠撳偍鎺ュ彛瀹炵幇锛堝疄鐜?domain.UserRepository锛?
鈹?  鈹溾攢鈹€ messaging/

鈹?  鈹?  鈹斺攢鈹€ RabbitMQEventPublisher.java  # 浜嬩欢鍙戝竷瀹炵幇锛堝疄鐜?domain.EventPublisher锛?
鈹?  鈹溾攢鈹€ external/

鈹?  鈹?  鈹斺攢鈹€ SmsAdapter.java              # 澶栭儴鏈嶅姟閫傞厤鍣紙濡傜煭淇″彂閫侊級

鈹?  鈹斺攢鈹€ config/

鈹?      鈹溾攢鈹€ MyBatisConfig.java           # MyBatis 閰嶇疆

鈹?      鈹斺攢鈹€ RabbitMQConfig.java          # RabbitMQ 閰嶇疆

鈹?
鈹斺攢鈹€ interfaces/                  # 鎺ュ彛閫傞厤灞傦紙渚濊禆 application锛?
&#x20;   鈹溾攢鈹€ rest/

&#x20;   鈹?  鈹斺攢鈹€ UserController.java          # REST 鎺ュ彛锛堣皟鐢?application 鏈嶅姟锛?
&#x20;   鈹斺攢鈹€ consumer/

&#x20;       鈹斺攢鈹€ OrderCreatedConsumer.java    # MQ 娑堣垂鑰咃紙璋冪敤 application 鏈嶅姟锛?```

### 8.3 鍏抽敭瀹炴柦绾︽潫



* `domain` 灞傜姝㈠嚭鐜颁换浣曟鏋舵敞瑙ｏ紙濡?`@Entity`銆乣@Mapper`銆乣@Component`銆乣@Autowired` 绛夛級

* `application` 灞傜姝㈢洿鎺ヨ闂暟鎹簱銆丮Q銆佸閮ㄦ湇鍔★紙閫氳繃 `domain` 鎺ュ彛闂存帴璁块棶锛?
* `infrastructure` 灞傜殑瀹炵幇绫诲繀椤婚€氳繃 Spring 娉ㄥ叆鍒?`application` 灞傦紝绂佹 `new` 鍏抽敭瀛楀垱寤?
* 璺ㄦ湇鍔￠€氫俊蹇呴』閫氳繃 API 缃戝叧鎴栨秷鎭槦鍒楋紝绂佹鏈嶅姟闂寸洿鎺ヨ皟鐢?
* 涓氬姟閫昏緫鍙樻洿浠呬慨鏀?`domain` 鎴?`application` 灞傦紝`infrastructure` 鍜?`interfaces` 灞傚敖閲忎笉鍙?


***

## 涔濄€佸畨鍏ㄩ厤缃疄鏂界粏鍒?
### 9.1 JWT 閰嶇疆瑙勮寖



| 閰嶇疆椤?                | 瑕佹眰                                          | 璇存槑                                     |
| ------------------- | ------------------------------------------- | -------------------------------------- |
| 绛惧悕绠楁硶                | HS256锛圚MAC-SHA256锛?                         | 瀵圭О鍔犲瘑绠楁硶锛岄儴缃茬畝鍗曪紝閫傚悎鍐呴儴寰湇鍔¤璇?                 |
| Token 浼犻€掓柟寮?         | \`Authorization: Bearer  OAuth 2.0 鏍囧噯锛岃姹傚ご鎼哄甫 |                                        |
| `access_token` 鏈夋晥鏈? | 30 鍒嗛挓锛?800 绉掞級                               | 鐭湡鏈夋晥锛岄檷浣庢硠闇查闄?                           |
| `refresh_token` 鏈夋晥鏈?| 7 澶?                                        | 闀挎湡鏈夋晥锛岀敤浜庡埛鏂?`access_token`               |
| 瀵嗛挜鏉ユ簮                | 鐜鍙橀噺 `JWT_SECRET`                           | 瀵嗛挜闀垮害 鈮?256 bits锛堝嵆 32 涓?ASCII 瀛楃锛夛紝绂佹纭紪鐮?|
| 瀵嗛挜杞崲                | 姣?90 澶╄疆鎹竴娆?                                 | 杞崲鏃堕渶鍏煎鏃у瘑閽ラ獙璇侊紙鍙屽瘑閽ュ叡瀛樿繃娓℃湡锛?                 |

#### JWT 閰嶇疆浠ｇ爜绀轰緥锛圫pring Security锛?


```
@Configuration

@EnableWebSecurity

public class JwtSecurityConfig extends WebSecurityConfigurerAdapter {

&#x20;   private final String jwtSecret;

&#x20;   private final long accessTokenExpireSeconds = 1800; // 30鍒嗛挓

&#x20;   private final long refreshTokenExpireSeconds = 60 \* 60 \* 24 \* 7; // 7澶?
&#x20;   // 浠庣幆澧冨彉閲忔敞鍏ュ瘑閽ワ紝绂佹纭紪鐮?
&#x20;   public JwtSecurityConfig(@Value("\${jwt.secret}") String jwtSecret) {

&#x20;       this.jwtSecret = jwtSecret;

&#x20;       // 鏍￠獙瀵嗛挜闀垮害

&#x20;       if (jwtSecret.length()  {

&#x20;           throw new IllegalArgumentException("JWT\_SECRET 闀垮害蹇呴』鈮?2瀛楃锛?56bits锛?);

&#x20;       }

&#x20;   }

&#x20;   // JWT 浠ょ墝鐢熸垚鍣?
&#x20;   @Bean

&#x20;   public JwtTokenProvider jwtTokenProvider() {

&#x20;       return new JwtTokenProvider(

&#x20;           jwtSecret,

&#x20;           accessTokenExpireSeconds,

&#x20;           refreshTokenExpireSeconds

&#x20;       );

&#x20;   }

&#x20;   // 瀵嗙爜鍔犲瘑鍣紙涓?9.3 瀵嗙爜绛栫暐涓€鑷达級

&#x20;   @Bean

&#x20;   public PasswordEncoder passwordEncoder() {

&#x20;       return new BCryptPasswordEncoder(10);

&#x20;   }

&#x20;   // 瀹夊叏瑙勫垯閰嶇疆

&#x20;   @Override

&#x20;   protected void configure(HttpSecurity http) throws Exception {

&#x20;       http

&#x20;           .csrf(csrf -> csrf.disable()) // 寰湇鍔￠棿璋冪敤绂佺敤 CSRF

&#x20;           .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 鏃犵姸鎬?
&#x20;           .authorizeRequests(auth -> auth

&#x20;               // 鏀捐鎺ュ彛锛堜笌 9.4 涓€鑷达級

&#x20;               .antMatchers("/actuator/health", "/actuator/prometheus").permitAll()

&#x20;               .antMatchers("/swagger-ui/\*\*", "/v3/api-docs/\*\*").permitAll()

&#x20;               .antMatchers(HttpMethod.POST, "/auth/login", "/auth/register").permitAll()

&#x20;               // 鍏朵粬鎺ュ彛闇€璁よ瘉

&#x20;               .anyRequest().authenticated()

&#x20;           )

&#x20;           // JWT 杩囨护鍣紙楠岃瘉 access\_token锛?
&#x20;           .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

&#x20;   }

&#x20;   // JWT 璁よ瘉杩囨护鍣紙鑷畾涔夊疄鐜帮級

&#x20;   @Bean

&#x20;   public JwtAuthenticationFilter jwtAuthenticationFilter() {

&#x20;       return new JwtAuthenticationFilter(jwtTokenProvider());

&#x20;   }

}
```

### 9.2 璁よ瘉鎺ュ彛瑙勮寖

#### 9.2.1 鐢ㄦ埛鐧诲綍



* **璇锋眰鏂瑰紡**锛歅OST

* **璇锋眰璺緞**锛歚/auth/login`

* **Content-Type**锛歛pplication/json

* **璇锋眰浣?*锛?


```
{

&#x20; "username": "string", // 鐢ㄦ埛鍚嶏紙鍞竴鏍囪瘑锛?
&#x20; "password": "string"  // 鏄庢枃瀵嗙爜锛堜紶杈撹繃绋嬮渶 HTTPS 鍔犲瘑锛?
}
```



* **鎴愬姛鍝嶅簲**锛?00 OK锛夛細



```
{

&#x20; "code": 200,

&#x20; "message": "OK",

&#x20; "data": {

&#x20;   "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", // JWT 璁块棶浠ょ墝

&#x20;   "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", // 鍒锋柊浠ょ墝

&#x20;   "expiresIn": 1800 // accessToken 鏈夋晥鏈燂紙绉掞級

&#x20; }

}
```



* **澶辫触鍝嶅簲**锛?01 Unauthorized锛夛細



```
{

&#x20; "code": 401,

&#x20; "message": "鐢ㄦ埛鍚嶆垨瀵嗙爜閿欒",

&#x20; "data": null

}
```

#### 9.2.2 鐢ㄦ埛娉ㄥ唽



* **璇锋眰鏂瑰紡**锛歅OST

* **璇锋眰璺緞**锛歚/auth/register`

* **Content-Type**锛歛pplication/json

* **璇锋眰浣?*锛堥渶婊¤冻 9.3 瀵嗙爜绛栫暐锛夛細



```
{

&#x20; "username": "string", // 鐢ㄦ埛鍚嶏紙2-20浣嶏紝鍞竴锛?
&#x20; "password": "string", // 瀵嗙爜锛?-32浣嶏紝鍚瓧姣?鏁板瓧锛?
&#x20; "email": "string"     // 閭锛堟牸寮忓悎娉曪紝鍞竴锛?
}
```



* **鎴愬姛鍝嶅簲**锛?00 OK锛夛細



```
{

&#x20; "code": 200,

&#x20; "message": "娉ㄥ唽鎴愬姛",

&#x20; "data": {

&#x20;   "userId": 1 // 娉ㄥ唽鐢熸垚鐨勭敤鎴稩D

&#x20; }

}
```



* **澶辫触鍝嶅簲**锛?00 Bad Request锛夛細



```
{

&#x20; "code": 400,

&#x20; "message": "閭鏍煎紡涓嶆纭紱瀵嗙爜闀垮害蹇呴』鍦?-32浣嶄箣闂?,

&#x20; "data": null

}
```

#### 9.2.3 鍒锋柊 Token



* **璇锋眰鏂瑰紡**锛歅OST

* **璇锋眰璺緞**锛歚/auth/refresh-token`

* **Content-Type**锛歛pplication/json

* **璇锋眰浣?*锛?


```
{

&#x20; "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

}
```



* **鎴愬姛鍝嶅簲**锛?00 OK锛夛細



```
{

&#x20; "code": 200,

&#x20; "message": "OK",

&#x20; "data": {

&#x20;   "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",

&#x20;   "expiresIn": 1800

&#x20; }

}
```

### 9.3 瀵嗙爜瀹夊叏绛栫暐



| 閰嶇疆椤?  | 瑕佹眰                | 瀹炴柦鏂瑰紡                        |
| ----- | ----------------- | --------------------------- |
| 鍔犲瘑绠楁硶  | BCrypt            | 涓嶅彲閫嗗姞鐩愬搱甯岋紝鑷甫闅忔満鐩?              |
| 鍔犲瘑寮哄害  | log rounds = 10   | 骞宠　瀹夊叏鎬т笌鎬ц兘锛堝€艰秺澶ц秺瀹夊叏浣嗚€楁椂鏇撮暱锛?      |
| 鏈€灏忛暱搴? | 鈮?8 浣?            | DTO 灞傞€氳繃 `@Size(min = 8)` 鏍￠獙 |
| 澶嶆潅搴﹁姹?| 蹇呴』鍖呭惈瀛楁瘝 + 鏁板瓧       | DTO 灞傞€氳繃 `@Pattern` 姝ｅ垯鏍￠獙     |
| 瀛樺偍鏍煎紡  | 瀛樺偍 BCrypt 鍔犲瘑鍚庣殑鍝堝笇鍊?| 绂佹瀛樺偍鏄庢枃鎴?MD5 绛夊急鍝堝笇            |
| 瀵嗙爜閲嶇疆  | 鐢熸垚涓存椂閾炬帴锛堟湁鏁堟湡 24 灏忔椂锛?| 绂佹鐩存帴杩斿洖鍘熷瀵嗙爜                  |

#### 瀵嗙爜鏍￠獙浠ｇ爜绀轰緥锛圖TO 灞傦級



```
public record RegisterUserCommand(

&#x20;   @NotBlank(message = "鐢ㄦ埛鍚嶄笉鑳戒负绌?)

&#x20;   @Size(min = 2, max = 20, message = "鐢ㄦ埛鍚嶉暱搴﹀繀椤诲湪2-20浣嶄箣闂?)

&#x20;   String username,

&#x20;   @NotBlank(message = "瀵嗙爜涓嶈兘涓虹┖")

&#x20;   @Size(min = 8, max = 32, message = "瀵嗙爜闀垮害蹇呴』鍦?-32浣嶄箣闂?)

&#x20;   @Pattern(regexp = "^(?=.\*\[A-Za-z])(?=.\*\\\d).+\$", message = "瀵嗙爜蹇呴』鍖呭惈瀛楁瘝鍜屾暟瀛?)

&#x20;   String password,

&#x20;   @NotBlank(message = "閭涓嶈兘涓虹┖")

&#x20;   @Email(message = "閭鏍煎紡涓嶆纭?)

&#x20;   String email

) {}
```

#### 瀵嗙爜鍔犲瘑浠ｇ爜绀轰緥锛圫ervice 灞傦級



```
@Service

public class AuthService {

&#x20;   private final PasswordEncoder passwordEncoder;

&#x20;   private final UserRepository userRepository;

&#x20;   private final JwtTokenProvider jwtTokenProvider;

&#x20;   // 鏋勯€犲嚱鏁版敞鍏ワ紙绂佹 @Autowired锛?
&#x20;   public AuthService(PasswordEncoder passwordEncoder, UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {

&#x20;       this.passwordEncoder = passwordEncoder;

&#x20;       this.userRepository = userRepository;

&#x20;       this.jwtTokenProvider = jwtTokenProvider;

&#x20;   }

&#x20;   public Long register(RegisterUserCommand command) {

&#x20;       // 1. 鏍￠獙閭鏄惁宸叉敞鍐?
&#x20;       if (userRepository.existsByEmail(command.email())) {

&#x20;           throw new BusinessException(ErrorCode.EMAIL\_ALREADY\_REGISTERED);

&#x20;       }

&#x20;       // 2. 瀵嗙爜鍔犲瘑锛圔Crypt 鑷姩鍔犵洂锛?
&#x20;       String encryptedPassword = passwordEncoder.encode(command.password());

&#x20;       // 3. 淇濆瓨鐢ㄦ埛

&#x20;       User user = User.builder()

&#x20;           .username(command.username())

&#x20;           .password(encryptedPassword) // 瀛樺偍鍔犲瘑鍚庣殑鍝堝笇鍊?
&#x20;           .email(command.email())

&#x20;           .status(UserStatus.ACTIVE)

&#x20;           .createdAt(LocalDateTime.now())

&#x20;           .updatedAt(LocalDateTime.now())

&#x20;           .deleted(false)

&#x20;           .build();

&#x20;       userRepository.save(user);

&#x20;       return user.getId();

&#x20;   }

&#x20;   public JwtTokenPair login(LoginCommand command) {

&#x20;       // 1. 鏌ヨ鐢ㄦ埛

&#x20;       User user = userRepository.findByUsername(command.username())

&#x20;           .orElseThrow(() -> new BusinessException(ErrorCode.USER\_NOT\_FOUND));

&#x20;       // 2. 鏍￠獙瀵嗙爜锛堟槑鏂囦笌鍝堝笇鍊兼瘮瀵癸級

&#x20;       if (!passwordEncoder.matches(command.password(), user.getPassword())) {

&#x20;           throw new BusinessException(ErrorCode.PASSWORD\_INCORRECT);

&#x20;       }

&#x20;       // 3. 鐢熸垚 Token 瀵?
&#x20;       return jwtTokenProvider.generateTokenPair(user);

&#x20;   }

}
```

### 9.4 鎺ュ彛鏀捐瑙勫垯锛堟棤闇€璁よ瘉锛?


| 璇锋眰鏂规硶 | 璺緞                     | 鐢ㄩ€?          | 瀹夊叏璇存槑                      |
| ---- | ---------------------- | ------------ | ------------------------- |
| GET  | `/actuator/health`     | 鏈嶅姟鍋ュ悍妫€鏌?      | 鏃犳晱鎰熶俊鎭紝鍏紑璁块棶                |
| GET  | `/actuator/prometheus` | 鐩戞帶鎸囨爣閲囬泦       | 浠呮毚闇查潪鏁忔劅鎸囨爣锛岀敓浜х幆澧冮渶闄愬埗 IP       |
| GET  | `/swagger-ui/**`       | API 鏂囨。椤甸潰     | 浠呭紑鍙?/ 娴嬭瘯鐜鍚敤              |
| GET  | `/v3/api-docs/**`      | OpenAPI 鍗忚鏂囦欢 | 浠呭紑鍙?/ 娴嬭瘯鐜鍚敤              |
| POST | `/auth/login`          | 鐢ㄦ埛鐧诲綍         | 鍏紑璁块棶锛岄渶 HTTPS 鍔犲瘑           |
| POST | `/auth/register`       | 鐢ㄦ埛娉ㄥ唽         | 鍏紑璁块棶锛岄渶鍙傛暟鏍￠獙                |
| POST | `/auth/refresh-token`  | 鍒锋柊 Token     | 鍏紑璁块棶锛岄渶鏍￠獙 refreshToken 鏈夋晥鎬?|

### 9.5 瀹夊叏绾㈢嚎锛堜弗鏍肩姝級



1. **绂佹鍦?JWT 涓瓨鏀炬晱鎰熶俊鎭?*锛氬寘鎷瘑鐮併€佽韩浠借瘉鍙枫€佹墜鏈哄彿銆侀摱琛屽崱鍙风瓑锛屼粎鍏佽瀛樻斁鐢ㄦ埛 ID銆佺敤鎴峰悕绛夐潪鏁忔劅鏍囪瘑

2. **绂佹鎺ュ彛杩斿洖鏄庢枃瀵嗙爜**锛氭棤璁烘垚鍔?/ 澶辫触鍝嶅簲锛屽潎涓嶅緱鍖呭惈鏄庢枃瀵嗙爜锛堝鐧诲綍澶辫触鎻愮ず 鈥滃瘑鐮侀敊璇€?鑰岄潪 鈥滃瘑鐮?123456 閿欒鈥濓級

3. **绂佹鏃ュ織杈撳嚭鏁忔劅淇℃伅**锛歍oken銆佸瘑鐮併€佹墜鏈哄彿绛夐渶鑴辨晱鍚庤緭鍑猴紙濡?Token 鍙繚鐣欏墠 6 浣嶅拰鍚?4 浣嶏級

4. **绂佹纭紪鐮佸瘑閽?*锛欽WT\_SECRET銆佹暟鎹簱瀵嗙爜绛夊繀椤讳粠鐜鍙橀噺鎴栭厤缃腑蹇冭鍙栵紝绂佹鍐欐鍦ㄤ唬鐮?/ 閰嶇疆鏂囦欢涓?
5. **绂佹闈?HTTPS 浼犺緭**锛氱敓浜х幆澧冩墍鏈夋帴鍙ｅ繀椤婚€氳繃 HTTPS 浼犺緭锛岄槻姝㈡暟鎹獌鍚?
6. **绂佹寮卞瘑鐮佺瓥鐣?*锛氫笉寰楅檷浣庡瘑鐮侀暱搴︺€佸鏉傚害瑕佹眰锛屼笉寰椾娇鐢?MD5銆丼HA-1 绛夊急鍝堝笇绠楁硶

7. **绂佹鐩存帴鏆撮湶 Actuator 鎺ュ彛**锛氱敓浜х幆澧冮渶閫氳繃 IP 鐧藉悕鍗曢檺鍒?Actuator 璁块棶锛岀姝㈠叏缃戝叕寮€

#### 鏁忔劅淇℃伅鑴辨晱绀轰緥锛堟棩蹇楄緭鍑猴級



```
// 鉁?姝ｇ‘锛圱oken 鑴辨晱锛?
log.info("鐢ㄦ埛鐧诲綍鎴愬姛锛寀serId: {}, token: {}\*\*\*\*{}",&#x20;

&#x20;   userId,&#x20;

&#x20;   token.substring(0, 6),&#x20;

&#x20;   token.substring(token.length() - 4)

);

// 鉂?閿欒锛堣緭鍑哄畬鏁?Token锛?
log.info("鐢ㄦ埛鐧诲綍鎴愬姛锛宼oken: {}", token);
```



```
```
