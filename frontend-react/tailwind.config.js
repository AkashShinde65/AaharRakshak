/** @type {import('tailwindcss').Config} */
export default {
  darkMode: 'class',
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      colors: {
        brand: {
          50: '#ecfdf3',
          100: '#d1fae0',
          200: '#a7f3c4',
          300: '#6ee79b',
          400: '#34d36f',
          500: '#16b85c',
          600: '#0c9146',
          700: '#08733a',
          800: '#075c31',
          900: '#064c2a',
          950: '#032a17',
        },
        harvest: {
          50: '#fff7ed',
          100: '#ffedd5',
          200: '#fed7aa',
          300: '#fdba74',
          400: '#fb923c',
          500: '#f97316',
          600: '#ea580c',
          700: '#c2410c',
          800: '#9a3412',
          900: '#7c2d12',
          950: '#431407',
        },
      },
      boxShadow: {
        soft: '0 16px 42px -30px rgba(15, 23, 42, 0.35)',
      },
      fontFamily: {
        sans: ['Inter', 'ui-sans-serif', 'system-ui', 'sans-serif'],
      },
    },
  },
  plugins: [],
};
