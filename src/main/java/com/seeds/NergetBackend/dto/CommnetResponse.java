@Getter
@AllArgsConstructor
public class CommentResponseDto {
    private Long commentId;
    private String content;
    private String authorNickname;
    private LocalDateTime createdAt;
}
