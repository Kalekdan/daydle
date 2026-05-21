import { createContext, useContext, useEffect, useMemo, useState } from 'react'
import { api, type User } from './api'

type AuthContextValue = {
  token: string | null
  user: User | null
  loading: boolean
  setSession: (token: string) => Promise<void>
  logout: () => void
}

const AuthContext = createContext<AuthContextValue | null>(null)

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [token, setToken] = useState<string | null>(() => localStorage.getItem('daydle_token'))
  const [user, setUser] = useState<User | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let cancelled = false

    async function hydrate() {
      if (!token) {
        setUser(null)
        setLoading(false)
        return
      }

      try {
        const me = await api.me(token)
        if (!cancelled) {
          setUser(me)
        }
      } catch {
        if (!cancelled) {
          localStorage.removeItem('daydle_token')
          setToken(null)
          setUser(null)
        }
      } finally {
        if (!cancelled) {
          setLoading(false)
        }
      }
    }

    hydrate()
    return () => {
      cancelled = true
    }
  }, [token])

  const value = useMemo<AuthContextValue>(
    () => ({
      token,
      user,
      loading,
      setSession: async (nextToken: string) => {
        localStorage.setItem('daydle_token', nextToken)
        setToken(nextToken)
        const me = await api.me(nextToken)
        setUser(me)
      },
      logout: () => {
        localStorage.removeItem('daydle_token')
        setToken(null)
        setUser(null)
      },
    }),
    [loading, token, user],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) {
    throw new Error('useAuth must be used within AuthProvider')
  }
  return ctx
}
