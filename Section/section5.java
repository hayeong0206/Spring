//회원 컨트롤러에 의존관계 추가
@Controller
public class MemberController {

    //private final MemberService memberService = new MemberService();
    // 이런 식으로 저장할 필요 X -> 매번 새로 불러올 필요가 없음
    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }
    // 오류 'MemberService' 타입의 bean을 찾을 수 없습니다 -> 단순한 자바 클래스이기 때문
    // Service에 @Service 추가, Repository에 @Repository 추가
}

@Service
public class MemberService {
    private final MemberRepository memberRepository

    @Autowired
    public MemberService(MemoryMemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
 '''

@Repository
public class MemoryMemberRepository implements MemberRepository{
    '''
}

//자바 코드로 직접 스프링빈 등록하기
@Configuration
public class SpringConfig {

    @Bean
    public MemberService memberService() {
        return new MemberService(memberRepository());
    }

    @Bean
    public MemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }
}
