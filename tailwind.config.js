module.exports = {
    mode: 'jit',
    purge: [
        './public/**/*.html'
    ],
    darkMode: 'off', // or 'media' or 'class'
    theme: {
        extend: {
            container: {
                center: true,
                padding: '2rem',
            },
            p: {
                text: 'justify'
            },
            typography: {
                light: {
                    css: {
                        color: 'white',
                        h1: {color: 'white'},
                        h2: {color: 'white'},
                        h3: {color: 'white'},
                        a: {color: 'white'}
                    }
                },
            }
        },
    },
    variants: {
        extend: {
            typography: ['dark']
        },
    },
    plugins: [
        require('@tailwindcss/typography')
    ],
}
