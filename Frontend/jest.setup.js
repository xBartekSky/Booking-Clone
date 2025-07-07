import fetchMock from "jest-fetch-mock";
import React from "react";
import { TextEncoder, TextDecoder } from "util";

global.TextEncoder = TextEncoder;
global.TextDecoder = TextDecoder;

global.React = React;

fetchMock.enableMocks();
