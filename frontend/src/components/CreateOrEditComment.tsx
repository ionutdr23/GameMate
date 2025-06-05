import { useAxiosWithAuth } from "@/lib/utils";
import { useState } from "react";

const CreateOrEditComment = ({
  postId,
  parentCommentId = null,
  onCommentCreated,
}) => {
  const [content, setContent] = useState("");
  const axiosInstance = useAxiosWithAuth();

  const handleSubmit = async () => {
    try {
      const res = await axiosInstance.post(
        `/api/social/post/${postId}/comment`,
        {
          content,
          parentCommentId,
        }
      );
      setContent("");
      if (onCommentCreated) onCommentCreated((prev) => [...prev, res.data]);
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div className="my-2">
      <textarea
        value={content}
        onChange={(e) => setContent(e.target.value)}
        className="w-full border rounded p-2"
        placeholder="Write a comment..."
      />
      <button
        onClick={handleSubmit}
        className="mt-1 px-4 py-2 bg-blue-500 text-white rounded"
      >
        Comment
      </button>
    </div>
  );
};

export default CreateOrEditComment;
