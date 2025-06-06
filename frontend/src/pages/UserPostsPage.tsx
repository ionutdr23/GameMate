import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { useAxiosWithAuth } from "@/lib/utils";

type Post = {
  id: string;
  content: string;
  createdAt: string;
};

const UserPostsPage = () => {
  const { profileId } = useParams();
  const [posts, setPosts] = useState<Post[]>([]);
  const [page, setPage] = useState(0);
  const size = 10;
  const axiosInstance = useAxiosWithAuth();

  useEffect(() => {
    if (!profileId) return;
    const fetchPosts = async () => {
      try {
        const res = await axiosInstance.get(
          `social/user/${profileId}/posts?page=${page}&size=${size}`
        );
        setPosts(res.data.content);
      } catch (error) {
        console.error(error);
      }
    };
    fetchPosts();
  }, [profileId, page, axiosInstance]);

  return (
    <div className="max-w-2xl mx-auto p-4 space-y-4">
      {posts.map((post) => (
        <div key={post.id} className="p-4 border rounded shadow">
          <p>{post.content}</p>
          <small className="text-gray-500">
            {new Date(post.createdAt).toLocaleString()}
          </small>
        </div>
      ))}
      <div className="flex justify-between mt-4">
        <button
          onClick={() => setPage((p) => Math.max(0, p - 1))}
          disabled={page === 0}
          className="px-3 py-1 bg-gray-200 rounded"
        >
          Previous
        </button>
        <button
          onClick={() => setPage((p) => p + 1)}
          className="px-3 py-1 bg-gray-200 rounded"
        >
          Next
        </button>
      </div>
    </div>
  );
};

export default UserPostsPage;
