import type { ContributionDay } from '../api'

type Props = {
  days: ContributionDay[]
}

export function Heatmap({ days }: Props) {
  const color = (intensity: number) => {
    if (intensity <= 0) return '#f1efe8'
    if (intensity === 1) return '#e6d9c1'
    if (intensity === 2) return '#d6b57a'
    if (intensity === 3) return '#be8a3d'
    return '#8f5a1c'
  }

  return (
    <div className="heatmap">
      {days.map((day) => (
        <div
          key={day.date}
          className="heatmap-cell"
          style={{ background: color(day.intensity) }}
          title={`${day.date}: ${day.played ? `intensity ${day.intensity}` : 'no play'}`}
        />
      ))}
    </div>
  )
}
