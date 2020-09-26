/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

const mix = require("laravel-mix");
const SRC_PATH = "src";
const DIST_PATH = "dist";

mix.setResourceRoot("/");
mix.setPublicPath(DIST_PATH);

// React-TypeScript
// TypeScript 需要配置 ts-loader，如果只是纯 ts 文件（不是 tsx）可以直接使用 mix.ts 进行编译
mix.js(`${SRC_PATH}/js/index.tsx`, `${DIST_PATH}/js/app.js`).webpackConfig({
    module: {
        rules: [
            {
                test: /\.tsx?$/,
                loader: "ts-loader",
                exclude: /node_modules/,
            },
        ],
    },
    resolve: {
        extensions: ["*", ".js", ".jsx", ".ts", ".tsx"],
    },
});

// React-JavaScript
// mix.react(`${SRC_PATH}/index.tsx`, `${DIST_PATH}/js/app.js`)

mix.sass(`${SRC_PATH}/sass/app.scss`, `${DIST_PATH}/css/app.css`);
