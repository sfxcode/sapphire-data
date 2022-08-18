import {defineConfig} from 'vitepress'

export default defineConfig({
    lang: 'en-US',
    title: 'sapphire-data',
    description: 'Scala Adapter for data with key value handling.',

    lastUpdated: true,
    cleanUrls: 'without-subfolders',

    themeConfig: {
        logo: '/sfxcode.png',

        nav: nav(),

        sidebar: {
            '/guide/': sidebarGuide(),
        },

        editLink: {
            pattern: 'https://github.com/sfxcode/sapphire-data/edit/main/docs/:path',
            text: 'Edit this page on GitHub'
        },

        socialLinks: [
            { icon: 'github', link: 'https://github.com/sfxcode/sapphire-data' }
        ],

        footer: {
            message: 'Released under the Apache License 2.0.',
            copyright: 'Copyright © 2022 - SFXCode'
        },

    }
})

function nav() {
    return [
        { text: 'Guide', link: '/guide/', activeMatch: '/guide/' },
        {
            text: 'Changelog',
            link: 'https://github.com/sfxcode/sapphire-data/blob/main/CHANGES.md'
        }
    ]
}

function sidebarGuide() {
    return [
        {
            text: 'Introduction',
            collapsible: true,
            items: [
                { text: 'About', link: '/guide/' },
                { text: 'Getting Started', link: '/guide/getting-started' },
            ]
        },
        {
            text: 'Additional Features',
            collapsible: true,
            items: [
                { text: 'Expression Language', link: '/guide/expression-language' },
                { text: 'Reports', link: '/guide/reports' },
            ]
        },

    ]
}

