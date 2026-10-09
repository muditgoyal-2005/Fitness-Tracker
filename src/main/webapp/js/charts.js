(() => {
  const palette = { lime: '#c6ff3d', violet: '#9c83ff', coral: '#ff806b', muted: '#879188', grid: 'rgba(220,235,220,.09)' };
  const chartDefaults = () => {
    if (!window.Chart) return;
    Chart.defaults.color = palette.muted;
    Chart.defaults.font.family = 'JetBrains Mono, monospace';
    Chart.defaults.font.size = 9;
    Chart.defaults.plugins.legend.labels.usePointStyle = true;
    Chart.defaults.plugins.legend.labels.boxWidth = 7;
  };
  const getJson = async (url) => {
    if (!url) return null;
    const response = await fetch(url, { headers: { Accept: 'application/json' }, cache: 'no-store' });
    if (!response.ok) throw new Error('Statistics request failed');
    return response.json();
  };

  document.addEventListener('DOMContentLoaded', async () => {
    if (!window.Chart || !window.FITNESS_API) return;
    chartDefaults();
    try {
      const stats = await getJson(window.FITNESS_API);
      const weekly = document.getElementById('weeklyChart');
      if (weekly && stats.weeklyCalories) new Chart(weekly, {
        type: 'bar',
        data: { labels: stats.labels, datasets: [{ data: stats.weeklyCalories, backgroundColor: stats.weeklyCalories.map((_, index) => index === stats.weeklyCalories.length - 1 ? palette.lime : 'rgba(198,255,61,.28)'), borderRadius: 4, maxBarThickness: 25 }] },
        options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { display: false }, tooltip: { displayColors: false } }, scales: { x: { grid: { display: false }, border: { display: false } }, y: { beginAtZero: true, grid: { color: palette.grid }, border: { display: false }, ticks: { maxTicksLimit: 4 } } } }
      });
      const types = document.getElementById('typeChart');
      if (types && stats.types) {
        const labels = Object.keys(stats.types);
        const values = Object.values(stats.types);
        new Chart(types, { type: 'doughnut', data: { labels, datasets: [{ data: values, backgroundColor: [palette.lime, palette.violet, palette.coral, '#74c6ab', '#e2cf78', '#6b8796'], borderColor: '#1a1f1b', borderWidth: 3, hoverOffset: 5 }] }, options: { responsive: true, maintainAspectRatio: false, cutout: '75%', plugins: { legend: { position: 'bottom', labels: { padding: 13 } } } } });
      }
      const progress = document.getElementById('progressChart');
      if (progress && stats.weeklyCalories) {
        const cumulative = stats.weeklyCalories.reduce((result, value) => { result.push((result.at(-1) || 0) + value); return result; }, []);
        new Chart(progress, { type: 'line', data: { labels: stats.labels, datasets: [{ label: 'Cumulative kcal', data: cumulative, borderColor: palette.violet, backgroundColor: 'rgba(156,131,255,.12)', fill: true, tension: .36, pointRadius: 3, pointBackgroundColor: palette.lime, borderWidth: 2 }] }, options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { display: false } }, scales: { x: { grid: { display: false }, border: { display: false } }, y: { beginAtZero: true, grid: { color: palette.grid }, border: { display: false }, ticks: { maxTicksLimit: 4 } } } } });
      }
      const admin = document.getElementById('adminChart');
      if (admin) new Chart(admin, { type: 'bar', data: { labels: ['Members', 'Workout logs', 'Challenge entries', 'Review queue'], datasets: [{ data: [stats.users, stats.workouts, stats.participants, stats.pending], backgroundColor: [palette.coral, palette.lime, palette.violet, '#f0c76e'], borderRadius: 5, maxBarThickness: 52 }] }, options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { display: false }, tooltip: { displayColors: false } }, scales: { x: { grid: { display: false }, border: { display: false } }, y: { beginAtZero: true, grid: { color: palette.grid }, border: { display: false }, ticks: { precision: 0, maxTicksLimit: 5 } } } } });
    } catch (error) {
      document.querySelectorAll('.chart-wrap canvas,.type-chart-wrap canvas').forEach((canvas) => {
        const context = canvas.getContext('2d');
        if (context) {
          context.fillStyle = palette.muted;
          context.font = '11px JetBrains Mono';
          context.fillText('Chart data is temporarily unavailable', 12, 24);
        }
      });
      console.warn('Fitness charts could not load.', error);
    }
  });
})();
