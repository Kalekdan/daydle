import { useMemo, useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { api } from '../api'
import { useAuth } from '../auth'
import { Heatmap } from '../components/Heatmap'

export function HomePage() {
  const { token, user, logout } = useAuth()
  const [text, setText] = useState('')
  const [selectedGame, setSelectedGame] = useState('wordle')
  const [year, setYear] = useState(new Date().getFullYear())
  const [search, setSearch] = useState('')
  const [message, setMessage] = useState<string | null>(null)

  const gamesQuery = useQuery({ queryKey: ['games'], queryFn: api.games })

  const contributionQuery = useQuery({
    queryKey: ['contribution', selectedGame, year, token],
    queryFn: () => api.contribution(token!, selectedGame, year),
    enabled: Boolean(token),
  })

  const overviewQuery = useQuery({
    queryKey: ['overview', token],
    queryFn: () => api.overview(token!),
    enabled: Boolean(token),
  })

  const followingQuery = useQuery({
    queryKey: ['following', token],
    queryFn: () => api.following(token!),
    enabled: Boolean(token),
  })

  const usersQuery = useQuery({
    queryKey: ['users', search, token],
    queryFn: () => api.users(token!, search),
    enabled: Boolean(token),
  })

  const feedQuery = useQuery({
    queryKey: ['feed', selectedGame, token],
    queryFn: () => api.feed(token!, selectedGame),
    enabled: Boolean(token),
  })

  const followingSet = useMemo(() => new Set((followingQuery.data || []).map((u) => u.id)), [followingQuery.data])

  async function previewAndSave() {
    if (!token || !text.trim()) return
    try {
      const preview = await api.parsePreview(token, text)
      const finalKey = preview.selectedGameKey
      await api.saveResult(token, text, finalKey)
      setMessage(`Saved as ${finalKey}`)
      setText('')
      contributionQuery.refetch()
      overviewQuery.refetch()
      feedQuery.refetch()
    } catch (err) {
      setMessage(err instanceof Error ? err.message : 'Failed to save result')
    }
  }

  async function toggleFollow(userId: number) {
    if (!token) return
    if (followingSet.has(userId)) {
      await api.unfollow(token, userId)
    } else {
      await api.follow(token, userId)
    }
    followingQuery.refetch()
    feedQuery.refetch()
  }

  return (
    <main className="home">
      <header className="topbar panel">
        <div>
          <h1>Daydle</h1>
          <p>Welcome, {user?.username}</p>
        </div>
        <button onClick={logout}>Log out</button>
      </header>

      <section className="panel">
        <h2>Paste game results</h2>
        <textarea
          value={text}
          onChange={(e) => setText(e.target.value)}
          placeholder="Paste Wordle / Connections / Maptap / Parseword share text"
          rows={6}
        />
        <button onClick={previewAndSave}>Detect and save</button>
        {message && <p className="status">{message}</p>}
      </section>

      <section className="panel">
        <h2>Contributions</h2>
        <div className="inline-controls">
          <select value={selectedGame} onChange={(e) => setSelectedGame(e.target.value)}>
            {(gamesQuery.data || []).map((game) => (
              <option key={game.key} value={game.key}>
                {game.displayName}
              </option>
            ))}
          </select>
          <input type="number" value={year} onChange={(e) => setYear(Number(e.target.value))} />
        </div>
        {contributionQuery.data && <Heatmap days={contributionQuery.data.days} />}
        <div className="stats-row">
          <strong>Total plays:</strong> {overviewQuery.data?.totalPlays ?? '-'}
          <strong>Current streak:</strong> {overviewQuery.data?.currentStreak ?? '-'}
          <strong>Longest streak:</strong> {overviewQuery.data?.longestStreak ?? '-'}
        </div>
      </section>

      <section className="panel split">
        <div>
          <h2>Find users</h2>
          <input
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search by username"
          />
          <div className="list">
            {(usersQuery.data || []).map((person) => (
              <div key={person.id} className="row">
                <span>{person.username}</span>
                <button onClick={() => toggleFollow(person.id)}>
                  {followingSet.has(person.id) ? 'Unfollow' : 'Follow'}
                </button>
              </div>
            ))}
          </div>
        </div>
        <div>
          <h2>Following feed</h2>
          <div className="list">
            {(feedQuery.data || []).map((item) => (
              <div key={item.resultId} className="feed-item">
                <strong>{item.username}</strong> posted {item.gameKey} for {item.playedOn}
              </div>
            ))}
            {(feedQuery.data || []).length === 0 && <p>No feed entries yet.</p>}
          </div>
        </div>
      </section>
    </main>
  )
}
