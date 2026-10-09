(() => {
  const onReady = (callback) => document.readyState === 'loading' ? document.addEventListener('DOMContentLoaded', callback) : callback();

  onReady(() => {
    const body = document.body;
    const savedTheme = localStorage.getItem('pulse-theme');
    if (savedTheme) body.dataset.theme = savedTheme;

    const themeToggle = document.getElementById('themeToggle');
    themeToggle?.addEventListener('click', () => {
      const theme = body.dataset.theme === 'light' ? 'dark' : 'light';
      if (theme === 'dark') delete body.dataset.theme;
      else body.dataset.theme = theme;
      localStorage.setItem('pulse-theme', theme);
    });

    document.querySelectorAll('.toast-close').forEach((button) => button.addEventListener('click', () => button.closest('.toast')?.remove()));
    document.querySelectorAll('.toast').forEach((toast) => window.setTimeout(() => toast.remove(), 7000));

    document.querySelectorAll('[data-open-dialog]').forEach((button) => button.addEventListener('click', () => {
      document.getElementById(button.dataset.openDialog)?.showModal();
    }));
    document.querySelectorAll('[data-close-dialog]').forEach((button) => button.addEventListener('click', () => button.closest('dialog')?.close()));
    document.querySelectorAll('dialog').forEach((dialog) => dialog.addEventListener('click', (event) => {
      if (event.target === dialog) dialog.close();
    }));

    const workoutDialog = document.getElementById('workoutDialog');
    document.querySelectorAll('.edit-workout').forEach((button) => button.addEventListener('click', () => {
      document.getElementById('editWorkoutId').value = button.dataset.id;
      document.getElementById('editWorkoutType').value = button.dataset.type;
      document.getElementById('editWorkoutDuration').value = button.dataset.duration;
      document.getElementById('editWorkoutIntensity').value = button.dataset.intensity;
      document.getElementById('editWorkoutDate').value = button.dataset.date;
      document.getElementById('editWorkoutNotes').value = button.dataset.notes || '';
      workoutDialog?.showModal();
    }));

    const userDialog = document.getElementById('createUserDialog');
    document.querySelectorAll('.edit-user').forEach((button) => button.addEventListener('click', () => {
      document.getElementById('adminUserTitle').textContent = 'Edit member';
      document.getElementById('adminUserId').value = button.dataset.id;
      document.getElementById('adminUserName').value = button.dataset.name;
      document.getElementById('adminUserEmail').value = button.dataset.email;
      document.getElementById('adminUserRole').value = button.dataset.role;
      userDialog?.showModal();
    }));
    document.querySelector('[data-open-dialog="createUserDialog"]')?.addEventListener('click', () => {
      document.getElementById('adminUserTitle').textContent = 'Create member';
      document.getElementById('adminUserId').value = '';
      document.getElementById('adminUserName').value = '';
      document.getElementById('adminUserEmail').value = '';
      document.getElementById('adminUserRole').value = 'USER';
    });

    const intensity = document.getElementById('intensitySelect');
    const intensityValue = document.getElementById('intensityValue');
    intensity?.addEventListener('change', () => {
      intensityValue.textContent = intensity.value;
      intensityValue.dataset.level = intensity.value.toLowerCase();
    });

    document.querySelectorAll('form[data-confirm]').forEach((form) => form.addEventListener('submit', (event) => {
      if (form.dataset.confirmed === 'yes') {
        delete form.dataset.confirmed;
        return;
      }
      event.preventDefault();
      const prompt = form.dataset.confirm || 'Continue with this action?';
      const answer = window.confirm(prompt);
      if (answer) {
        form.dataset.confirmed = 'yes';
        form.requestSubmit(event.submitter);
      }
    }));

    if (window.lucide) window.lucide.createIcons();
    const counters = document.querySelectorAll('.count-up[data-value]');
    const animateCounter = (node) => {
      const target = Number(node.dataset.value);
      if (!Number.isFinite(target) || target === 0) return;
      const start = performance.now();
      const duration = 700;
      const draw = (now) => {
        const progress = Math.min(1, (now - start) / duration);
        node.textContent = Math.round(target * (1 - Math.pow(1 - progress, 3))).toLocaleString();
        if (progress < 1) requestAnimationFrame(draw);
      };
      requestAnimationFrame(draw);
    };
    if ('IntersectionObserver' in window) {
      const observer = new IntersectionObserver((entries) => entries.forEach((entry) => {
        if (entry.isIntersecting) {
          animateCounter(entry.target);
          observer.unobserve(entry.target);
        }
      }));
      counters.forEach((counter) => observer.observe(counter));
    } else counters.forEach(animateCounter);

    const feed = document.getElementById('activityFeed');
    if (feed?.dataset.endpoint) {
      let hydrated = !feed.querySelector('.activity-event');
      const poll = async () => {
        try {
          const response = await fetch(feed.dataset.endpoint, { headers: { Accept: 'application/json' }, cache: 'no-store' });
          if (!response.ok) return;
          const events = await response.json();
          if (!hydrated) {
            feed.replaceChildren();
            hydrated = true;
          }
          const existing = new Set(Array.from(feed.querySelectorAll('[data-event-id]'), (node) => node.dataset.eventId));
          events.slice().reverse().forEach((event) => {
            if (existing.has(String(event.id))) return;
            const article = document.createElement('article');
            article.className = 'activity-event';
            article.dataset.eventId = event.id;
            const marker = document.createElement('span');
            marker.className = 'activity-mark';
            marker.innerHTML = '<i data-lucide="activity"></i>';
            const body = document.createElement('div');
            const title = document.createElement('strong');
            title.textContent = event.action;
            const details = document.createElement('p');
            details.textContent = event.details;
            const meta = document.createElement('small');
            meta.textContent = `${event.user || 'System'} · ${event.createdAt}`;
            body.append(title, details, meta);
            article.append(marker, body);
            feed.prepend(article);
          });
          while (feed.querySelectorAll('[data-event-id]').length > 30) feed.lastElementChild?.remove();
          if (window.lucide) window.lucide.createIcons();
        } catch (error) {
          console.debug('Activity feed refresh deferred.', error);
        }
      };
      window.setInterval(poll, 5000);
    }
  });
})();
