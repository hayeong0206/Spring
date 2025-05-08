// 회원 객체
 public class Member {
	     private Long id;
	     private String name;
	     public Long getId() {
	         return id;
	}
	     public void setId(Long id) {
	         this.id = id;
	}
	public String getName() {
         return name;
	}
	     public void setName(String name) {
	         this.name = name;
	}
}

//회원 리포지토리 인터페이스
public interface MemberRepository {
	Member save(Member member);
	Optional<Member> findById(Long id);
	Optional<Member> findByName(String name);
	List<Member> findAll();
}

//회원 리포지토리 메모리 구현체
/**
 * 동시성 문제가 고려되어 있지 않음, 실무에서는 ConcurrentHashMap, AtomicLong 사용 고려 */
public class MemoryMemberRepository implements MemberRepository {
	private static Map<Long, Member> store = new HashMap<>();
	private static long sequence = 0L;
	@Override
	public Member save(Member member) {
		member.setId(++sequence);
		store.put(member.getId(), member);
		return member;
	}
	@Override
	public Optional<Member> findById(Long id) {
		return Optional.ofNullable(store.get(id));
	}
	@Override
	public List<Member> findAll() {
		return new ArrayList<>(store.values());
	}
	@Override
	public Optional<Member> findByName(String name) {
		return store.values().stream()
				.filter(member -> member.getName().equals(name))
				.findAny();
	}
	public void clearStore() {
		store.clear();
	}
}

//회원 리포지토리 테스트 케이스 작성
class MemoryMemberRepositoryTest {
	MemoryMemberRepository repository = new MemoryMemberRepository();
	@AfterEach
	public void afterEach() {
		repository.clearStore();
	}
	@Test
	public void save() {
//given
		Member member = new Member();
		member.setName("spring");
//when
		repository.save(member);
//then
		Member result = repository.findById(member.getId()).get();
		assertThat(result).isEqualTo(member);
	}
	@Test
	public void findByName() {
//given
		Member member1 = new Member();
		member1.setName("spring1");
		repository.save(member1);
		Member member2 = new Member();
		member2.setName("spring2");
		repository.save(member2);
//when
		Member result = repository.findByName("spring1").get();
//then
		assertThat(result).isEqualTo(member1);
	}
	@Test
	public void findAll() {
//given
		Member member1 = new Member();
		member1.setName("spring1");
		repository.save(member1);
		Member member2 = new Member();
		member2.setName("spring2");
		repository.save(member2);
//when
		List<Member> result = repository.findAll();
//then
		assertThat(result.size()).isEqualTo(2);
	}
}

//회원 서비스 개발
public class MemberService {
	private final MemberRepository memberRepository = new MemoryMemberRepository();
	/**
	 * 회원가입 */
	public Long join(Member member) {
		validateDuplicateMember(member); //중복 회원 검증 memberRepository.save(member);
		return member.getId();
	}
	private void validateDuplicateMember(Member member) {
		memberRepository.findByName(member.getName())
				.ifPresent(m -> {
					throw new IllegalStateException("이미 존재하는 회원입니다.");
				});
	}
	/**
	 * 전체 회원 조회 */
	public List<Member> findMembers() {
		return memberRepository.findAll();
	}
	public Optional<Member> findOne(Long memberId) {
		return memberRepository.findById(memberId);
	}
}

//회원 서비스 테스트
public class MemberService {
	private final MemberRepository memberRepository = new MemoryMemberRepository();

	public MemberService(MemoryMemberRepository memberRepository) {
	}

	/**
	 * 회원가입 */
	public Long join(Member member) {
		validateDuplicateMember(member); //중복 회원 검증 memberRepository.save(member);
		return member.getId();
	}
	private void validateDuplicateMember(Member member) {
		memberRepository.findByName(member.getName())
				.ifPresent(m -> {
					throw new IllegalStateException("이미 존재하는 회원입니다.");
				});
	}
	/**
	 * 전체 회원 조회 */
	public List<Member> findMembers() {
		return memberRepository.findAll();
	}
	public Optional<Member> findOne(Long memberId) {
		return memberRepository.findById(memberId);
	}
}