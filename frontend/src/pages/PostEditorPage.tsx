import { useState } from "react";
import { useAxiosWithAuth } from "@/lib/utils";

const PostEditorPage = () => {
  const [content, setContent] = useState("");
  const [tags, setTags] = useState("");
  const axiosInstance = useAxiosWithAuth();

  const handleSubmit = async () => {
    try {
      await axiosInstance.post(`/social/post`, {
        content,
        tags: tags.split(",").map((tag) => tag.trim()),
        visibility: "PUBLIC",
      });
      setContent("");
      setTags("");
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div className="max-w-xl mx-auto p-4">
      <textarea
        value={content}
        onChange={(e) => setContent(e.target.value)}
        className="w-full border rounded p-2"
        placeholder="What's on your mind?"
      />
      <input
        value={tags}
        onChange={(e) => setTags(e.target.value)}
        className="w-full mt-2 border rounded p-2"
        placeholder="Tags (comma-separated)"
      />
      <button
        onClick={handleSubmit}
        className="mt-2 bg-green-500 text-white px-4 py-2 rounded"
      >
        Post
      </button>
    </div>
  );
};

export default PostEditorPage;
