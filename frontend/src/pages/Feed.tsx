import { Card, CardContent } from "../components/ui/card";

type FeedProps = {
  own?: boolean;
};

const Feed: React.FC<FeedProps> = ({ own = false }) => {
  const posts = [
    {
      id: 1,
      user: "GamerJoe",
      content: "Anyone up for LoL tonight?",
      timestamp: "2h ago",
    },
    {
      id: 2,
      user: "PixelQueen",
      content: "Just hit Gold II in Fortnite!",
      timestamp: "5h ago",
    },
  ];

  return (
    <div className="p-8 max-w-2xl mx-auto text-white">
      <h2 className="text-2xl font-bold mb-4">
        {own ? "My Posts" : "Community Feed"}
      </h2>
      <div className="space-y-4">
        {posts.map((post) => (
          <Card key={post.id} className="bg-gray-800">
            <CardContent className="p-4">
              <p className="text-sm text-gray-400">
                @{post.user} · {post.timestamp}
              </p>
              <p className="text-white text-lg mt-1">{post.content}</p>
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  );
};

export default Feed;
