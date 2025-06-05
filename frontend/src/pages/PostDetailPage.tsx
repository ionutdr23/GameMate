import { useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import CommentThread from "@/components/CommentThread";
import ReactionBar from "@/components/ReactionBar";
import CreateOrEditComment from "@/components/CreateOrEditComment";
import { useAxiosWithAuth } from "@/lib/utils";

const PostDetailPage = () => {
  const { postId } = useParams();
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const [post, setPost] = useState<any>(null);
  const [comments, setComments] = useState([]);
  const axiosInstance = useAxiosWithAuth();

  useEffect(() => {
    const fetchData = async () => {
      if (postId) {
        try {
          const postRes = await axiosInstance.get(`/social/post/${postId}`);
          setPost(postRes.data);
          const commentsRes = await axiosInstance.get(
            `/social/post/${postId}/comments`
          );
          setComments(commentsRes.data);
        } catch (error) {
          console.error(error);
        }
      }
    };
    fetchData();
  }, [axiosInstance, postId]);

  if (!post) return <div>Loading...</div>;

  return (
    <div className="p-4 max-w-3xl mx-auto">
      <div className="bg-white shadow rounded-xl p-4 mb-4">
        <p>{post.content}</p>
        <ReactionBar postId={post.id} />
      </div>
      <CreateOrEditComment postId={post.id} onCommentCreated={setComments} />
      <CommentThread comments={comments} />
    </div>
  );
};

export default PostDetailPage;
