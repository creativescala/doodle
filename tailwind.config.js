/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./docs/src/templates/*.html",
    "./site/target/docs/site/**/*.html",
    "./project/CreativeScalaDirectives.scala",
  ],
  theme: {
    extend: {
      width: {
        "128": "32rem",
        "132": "33rem",
        "144": "36rem",
      },
    },
    fontFamily: {
      sans: ["Source Sans Pro", "sans-serif"],
      serif: ["Crimson Pro", "serif"],
    },
  },
  plugins: [],
};
