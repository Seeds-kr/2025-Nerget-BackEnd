@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public void createPost(PostRequestDto dto, MultipartFile imageFile, String userEmail) {
        Member author = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 회원이 존재하지 않습니다: " + userEmail));

        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setAuthor(author);

        // TODO: 이미지 업로드 후 URL 저장
        post.setImageUrl("이미지 URL 로직"); // 추후 imageService.upload(imageFile)

        LocalDateTime now = LocalDateTime.now();
        post.setCreatedAt(now);
        post.setUpdatedAt(now);

        post.setViewCount(0);
        post.setLikeCount(0);
        post.setCommentCount(0);

        postRepository.save(post);
    }

    // 게시글 목록 조회
    public List<PostResponseDto> getAllPosts() {
        List<Post> postList = postRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));

        return postList.stream()
                .map(post -> new PostResponseDto(
                        post.getPostId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getImageUrl(),
                        post.getAuthor().getNickname(),
                        post.getCreatedAt(),
                        post.getViewCount(),git
                        post.getLikeCount(),
                        post.getCommentCount()
                ))
                .toList();
    }

    public PostResponseDto getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다: " + id));

        // 조회수 증가
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);  // 업데이트 반영

        return new PostResponseDto(
                post.getPostId(),
                post.getTitle(),
                post.getContent(),
                post.getImageUrl(),
                post.getAuthor().getNickname(),
                post.getCreatedAt(),
                post.getViewCount(),
                post.getLikeCount(),
                post.getCommentCount()
        );
    }

}
