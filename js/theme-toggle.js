// Dark mode theme toggle with system preference detection and localStorage persistence
(function() {
  const STORAGE_KEY = 'creative-scala-theme';
  const html = document.documentElement;

  // Get stored theme or system preference
  function getPreferredTheme() {
    const stored = localStorage.getItem(STORAGE_KEY);
    if (stored) return stored;
    return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
  }

  // Update toggle button states
  function updateToggleButtons(isDark) {
    document.querySelectorAll('.toggle-button').forEach(button => {
      if (isDark) {
        button.classList.add('dark-mode');
      } else {
        button.classList.remove('dark-mode');
      }
    });
  }

  // Apply theme to document
  function applyTheme(theme) {
    const isDark = theme === 'dark';
    if (isDark) {
      html.classList.add('dark');
    } else {
      html.classList.remove('dark');
    }
    updateToggleButtons(isDark);
  }

  // Toggle theme
  function toggleTheme() {
    const currentTheme = html.classList.contains('dark') ? 'dark' : 'light';
    const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
    applyTheme(newTheme);
    localStorage.setItem(STORAGE_KEY, newTheme);
  }

  // Initialize theme on load
  applyTheme(getPreferredTheme());

  // Update toggle buttons after DOM is ready
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', function() {
      updateToggleButtons(html.classList.contains('dark'));
    });
  }

  // Expose toggle function globally for button onclick
  window.toggleTheme = toggleTheme;
})();
