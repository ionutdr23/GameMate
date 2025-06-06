import { Comment } from "@/types/comment";

const CommentThread = ({ comments }: { comments: Comment[] }) => {
  return (
    <div className="space-y-2">
      {comments.map((comment) => (
        <div key={comment.id} className="border rounded p-2">
          <p>{comment.content}</p>
          {comment.replies?.length > 0 && (
            <div className="ml-4 mt-2">
              <CommentThread comments={comment.replies} />
            </div>
          )}
        </div>
      ))}
    </div>
  );
};

export default CommentThread;
