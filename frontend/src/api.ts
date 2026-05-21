const API_BASE = (import.meta.env.VITE_API_BASE as string | undefined) ?? '/api'

export type AuthResponse = {
  id: number
  username: string
  email: string
  token: string
}

export type User = {
  id: number
  username: string
  email: string
  visibility: 'PUBLIC' | 'FOLLOWERS_ONLY' | 'PRIVATE'
}

export type Game = {
  key: string
  displayName: string
}

export type Candidate = {
  gameKey: string
  confidence: number
}

export type ParsePreview = {
  candidates: Candidate[]
  selectedGameKey: string
  normalized: Record<string, unknown>
}

export type ContributionDay = {
  date: string
  intensity: number
  played: boolean
}

export type ContributionResponse = {
  gameKey: string
  year: number
  days: ContributionDay[]
}

export type Overview = {
  totalPlays: number
  currentStreak: number
  longestStreak: number
}

export type FollowUser = {
  id: number
  username: string
}

export type FeedItem = {
  resultId: number
  userId: number
  username: string
  gameKey: string
  playedOn: string
  submittedAt: string
  parsed: Record<string, unknown>
}

async function request<T>(path: string, options: RequestInit = {}, token?: string): Promise<T> {
  const headers = new Headers(options.headers)
  headers.set('Content-Type', 'application/json')
  if (token) {
    headers.set('Authorization', `Bearer ${token}`)
  }

  const response = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers,
  })

  if (!response.ok) {
    const body = await response.json().catch(() => ({}))
    throw new Error(body.error || body.message || 'Request failed')
  }

  if (response.status === 204) {
    return {} as T
  }

  return (await response.json()) as T
}

export const api = {
  register: (payload: { username: string; email: string; password: string }) =>
    request<AuthResponse>('/auth/register', { method: 'POST', body: JSON.stringify(payload) }),

  login: (payload: { usernameOrEmail: string; password: string }) =>
    request<AuthResponse>('/auth/login', { method: 'POST', body: JSON.stringify(payload) }),

  me: (token: string) => request<User>('/auth/me', {}, token),

  games: () => request<Game[]>('/games'),

  parsePreview: (token: string, text: string) =>
    request<ParsePreview>('/results/parse-preview', { method: 'POST', body: JSON.stringify({ text }) }, token),

  saveResult: (token: string, text: string, gameKey?: string) =>
    request('/results', { method: 'POST', body: JSON.stringify({ text, gameKey }) }, token),

  contribution: (token: string, game: string, year: number) =>
    request<ContributionResponse>(`/stats/me/contributions?game=${encodeURIComponent(game)}&year=${year}`, {}, token),

  overview: (token: string) => request<Overview>('/stats/me/overview', {}, token),

  following: (token: string) => request<FollowUser[]>('/follows/me', {}, token),

  follow: (token: string, userId: number) => request(`/follows/${userId}`, { method: 'POST' }, token),

  unfollow: (token: string, userId: number) => request(`/follows/${userId}`, { method: 'DELETE' }, token),

  users: (token: string, query: string) =>
    request<FollowUser[]>(`/users?query=${encodeURIComponent(query)}`, {}, token),

  feed: (token: string, game?: string) =>
    request<FeedItem[]>(`/feed${game ? `?game=${encodeURIComponent(game)}` : ''}`, {}, token),
}
