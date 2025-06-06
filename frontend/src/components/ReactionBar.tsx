import React, { useEffect, useState, useCallback } from "react";
import { useAxiosWithAuth } from "@/lib/utils";

type ReactionCounts = Record<string, number>;

interface ReactionBarProps {
  postId: string;
}

const ReactionBar: React.FC<ReactionBarProps> = ({ postId }) => {
  const [counts, setCounts] = useState<ReactionCounts>({});
  const axiosInstance = useAxiosWithAuth();

  const fetchCounts = useCallback(async () => {
    await axiosInstance
      .get(`/api/social/post/${postId}/reactions/count`)
      .then((res) => setCounts(res.data.counts))
      .catch(console.error);
  }, [axiosInstance, postId]);

  useEffect(() => {
    fetchCounts();
  }, [fetchCounts]);

  const handleReact = async (type: string) => {
    try {
      await axiosInstance.post(`/api/social/post/${postId}/reaction`, { type });
      fetchCounts();
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div className="flex space-x-2 mt-2">
      {Object.entries(counts).map(([type, count]) => (
        <button
          key={type}
          onClick={() => handleReact(type)}
          className="text-sm px-2 py-1 border rounded"
        >
          {String(type)} ({Number(count)})
        </button>
      ))}
    </div>
  );
};

export default ReactionBar;
