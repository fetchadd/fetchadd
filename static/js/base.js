function debounce(func, wait) {
  var timeout;

  return function () {
    var context = this;
    var args = arguments;
    clearTimeout(timeout);

    timeout = setTimeout(function () {
      timeout = null;
      func.apply(context, args);
    }, wait);
  };
}

// Taken from mdbook
// The strategy is as follows:
// First, assign a value to each word in the document:
//  Words that correspond to search terms (stemmer aware): 40
//  Normal words: 2
//  First word in a sentence: 8
// Then use a sliding window with a constant number of words and count the
// sum of the values of the words within the window. Then use the window that got the
// maximum sum. If there are multiple maximas, then get the last one.
// Enclose the terms in <b>.
function makeTeaser(body, terms) {
  var TERM_WEIGHT = 40;
  var NORMAL_WORD_WEIGHT = 2;
  var FIRST_WORD_WEIGHT = 8;
  var TEASER_MAX_WORDS = 30;

  var stemmedTerms = terms.map(function (w) {
    return elasticlunr.stemmer(w.toLowerCase());
  });
  var termFound = false;
  var index = 0;
  var weighted = []; // contains elements of ["word", weight, index_in_document]

  // split in sentences, then words
  var sentences = body.toLowerCase().split(". ");

  for (var i in sentences) {
    var words = sentences[i].split(" ");
    var value = FIRST_WORD_WEIGHT;

    for (var j in words) {
      var word = words[j];

      if (word.length > 0) {
        for (var k in stemmedTerms) {
          if (elasticlunr.stemmer(word).startsWith(stemmedTerms[k])) {
            value = TERM_WEIGHT;
            termFound = true;
          }
        }
        weighted.push([word, value, index]);
        value = NORMAL_WORD_WEIGHT;
      }

      index += word.length;
      index += 1;  // ' ' or '.' if last word in sentence
    }

    index += 1;  // because we split at a two-char boundary '. '
  }

  if (weighted.length === 0) {
    return body;
  }

  var windowWeights = [];
  var windowSize = Math.min(weighted.length, TEASER_MAX_WORDS);
  // We add a window with all the weights first
  var curSum = 0;
  for (var i = 0; i < windowSize; i++) {
    curSum += weighted[i][1];
  }
  windowWeights.push(curSum);

  for (var i = 0; i < weighted.length - windowSize; i++) {
    curSum -= weighted[i][1];
    curSum += weighted[i + windowSize][1];
    windowWeights.push(curSum);
  }

  // If we didn't find the term, just pick the first window
  var maxSumIndex = 0;
  if (termFound) {
    var maxFound = 0;
    // backwards
    for (var i = windowWeights.length - 1; i >= 0; i--) {
      if (windowWeights[i] > maxFound) {
        maxFound = windowWeights[i];
        maxSumIndex = i;
      }
    }
  }

  var teaser = [];
  var startIndex = weighted[maxSumIndex][2];
  for (var i = maxSumIndex; i < maxSumIndex + windowSize; i++) {
    var word = weighted[i];
    if (startIndex < word[2]) {
      // missing text from index to start of `word`
      teaser.push(body.substring(startIndex, word[2]));
      startIndex = word[2];
    }

    // add <em/> around search terms
    if (word[1] === TERM_WEIGHT) {
      teaser.push("<b>");
    }
    startIndex = word[2] + word[0].length;
    teaser.push(body.substring(word[2], startIndex));

    if (word[1] === TERM_WEIGHT) {
      teaser.push("</b>");
    }
  }
  teaser.push("…");
  return teaser.join("");
}

function formatSearchResultItem(item, terms) {
  return '<div class="search-result-item">'
  + `<a href="${item.ref}">${item.doc.title}</a>`
  + `<div>${makeTeaser(item.doc.body, terms)}</div>`
  + '</div>';
}

function trimmerZh(token) {
    return token;
}

function stopWordFilterZh(token) {
    return token;
}

function stemmerZh(token) {
    return token;
}


function initSearch() {
  var $searchInput = document.getElementById("search");
  var $searchResults = document.querySelector(".search-result");
  var $searchResultsItems = document.querySelector(".search-result-items");
  var MAX_ITEMS = 10;

  var options = {
    bool: "AND",
    fields: {
      title: {boost: 2},
      body: {boost: 1},
    }
  };

  elasticlunr.Pipeline.registerFunction(trimmerZh, "trimmer-zh");
  elasticlunr.Pipeline.registerFunction(stopWordFilterZh, "stopWordFilter-zh");
  elasticlunr.Pipeline.registerFunction(stemmerZh, "stemmer-zh");

  var currentTerm = "";
  var index = elasticlunr.Index.load(window.searchIndex);

  $searchInput.addEventListener("keyup", debounce(function() {
    var term = $searchInput.value.trim();

    if (term === currentTerm || !index) {
      return;
    }
    $searchResults.style.display = term === "" ? "none" : "block";
    $searchResultsItems.innerHTML = "";
    if (term === "") {
      return;
    }

    var results = index.search(term, options);
    console.log(results);

    if (results.length === 0) {
      $searchResults.style.display = "none";
      return;
    }

    currentTerm = term;
    for (var i = 0; i < Math.min(results.length, MAX_ITEMS); i++) {
      var item = document.createElement("li");
      item.innerHTML = formatSearchResultItem(results[i], term.split(" "));
      $searchResultsItems.appendChild(item);
    }
  }, 150));
}

function initJumpPage() {
   var $pageInput = document.getElementById("page-jump-to");

   if ($pageInput) {
       $pageInput.addEventListener("keyup", function (event) {
           if (event.keyCode !== 13) {
               return;
           }

           var page = event.target.value.trim();

           if (parseInt(page)) {
               var url = window.location.href;

               if (url.indexOf("/page/") === -1) {
                   window.location.href = url + "page/" + page;
               } else {
                   window.location.href = url.substr(0, url.lastIndexOf("/page")) + "/page/"  + page;
               }
           }

       })
   }
}


function getViewport(){
    if (document.compatMode == "BackCompat"){
        return {
            width: document.body.clientWidth,
            height: document.body.clientHeight
        }
    } else {
        return {
            width: document.documentElement.clientWidth,
            height: document.documentElement.clientHeight
        }
    }
}


/*!
 * JavaScript Cookie v2.2.0
 * https://github.com/js-cookie/js-cookie
 *
 * Copyright 2006, 2015 Klaus Hartl & Fagner Brack
 * Released under the MIT license
 */
;(function (factory) {
    var registeredInModuleLoader;
    if (typeof define === 'function' && define.amd) {
        define(factory);
        registeredInModuleLoader = true;
    }
    if (typeof exports === 'object') {
        module.exports = factory();
        registeredInModuleLoader = true;
    }
    if (!registeredInModuleLoader) {
        var OldCookies = window.Cookies;
        var api = window.Cookies = factory();
        api.noConflict = function () {
            window.Cookies = OldCookies;
            return api;
        };
    }
}(function () {
    function extend () {
        var i = 0;
        var result = {};
        for (; i < arguments.length; i++) {
            var attributes = arguments[ i ];
            for (var key in attributes) {
                result[key] = attributes[key];
            }
        }
        return result;
    }

    function decode (s) {
        return s.replace(/(%[0-9A-Z]{2})+/g, decodeURIComponent);
    }

    function init (converter) {
        function api() {}

        function set (key, value, attributes) {
            if (typeof document === 'undefined') {
                return;
            }

            attributes = extend({
                path: '/'
            }, api.defaults, attributes);

            if (typeof attributes.expires === 'number') {
                attributes.expires = new Date(new Date() * 1 + attributes.expires * 864e+5);
            }

            // We're using "expires" because "max-age" is not supported by IE
            attributes.expires = attributes.expires ? attributes.expires.toUTCString() : '';

            try {
                var result = JSON.stringify(value);
                if (/^[\{\[]/.test(result)) {
                    value = result;
                }
            } catch (e) {}

            value = converter.write ?
                converter.write(value, key) :
                encodeURIComponent(String(value))
                    .replace(/%(23|24|26|2B|3A|3C|3E|3D|2F|3F|40|5B|5D|5E|60|7B|7D|7C)/g, decodeURIComponent);

            key = encodeURIComponent(String(key))
                .replace(/%(23|24|26|2B|5E|60|7C)/g, decodeURIComponent)
                .replace(/[\(\)]/g, escape);

            var stringifiedAttributes = '';
            for (var attributeName in attributes) {
                if (!attributes[attributeName]) {
                    continue;
                }
                stringifiedAttributes += '; ' + attributeName;
                if (attributes[attributeName] === true) {
                    continue;
                }

                // Considers RFC 6265 section 5.2:
                // ...
                // 3.  If the remaining unparsed-attributes contains a %x3B (";")
                //     character:
                // Consume the characters of the unparsed-attributes up to,
                // not including, the first %x3B (";") character.
                // ...
                stringifiedAttributes += '=' + attributes[attributeName].split(';')[0];
            }

            return (document.cookie = key + '=' + value + stringifiedAttributes);
        }

        function get (key, json) {
            if (typeof document === 'undefined') {
                return;
            }

            var jar = {};
            // To prevent the for loop in the first place assign an empty array
            // in case there are no cookies at all.
            var cookies = document.cookie ? document.cookie.split('; ') : [];
            var i = 0;

            for (; i < cookies.length; i++) {
                var parts = cookies[i].split('=');
                var cookie = parts.slice(1).join('=');

                if (!json && cookie.charAt(0) === '"') {
                    cookie = cookie.slice(1, -1);
                }

                try {
                    var name = decode(parts[0]);
                    cookie = (converter.read || converter)(cookie, name) ||
                        decode(cookie);

                    if (json) {
                        try {
                            cookie = JSON.parse(cookie);
                        } catch (e) {}
                    }

                    jar[name] = cookie;

                    if (key === name) {
                        break;
                    }
                } catch (e) {}
            }

            return key ? jar[key] : jar;
        }

        api.set = set;
        api.get = function (key) {
            return get(key, false /* read as raw */);
        };
        api.getJSON = function (key) {
            return get(key, true /* read as json */);
        };
        api.remove = function (key, attributes) {
            set(key, '', extend(attributes, {
                expires: -1
            }));
        };

        api.defaults = {};

        api.withConverter = init;

        return api;
    }

    return init(function () {});
}));

function initToc() {
    var blogContentEl = $("#blog-content");
    var blogTocEl = $("#blog-toc");
    var toggleBtn = $("#toggle-toc");

    if (blogContentEl == null || blogTocEl == null || toggleBtn == null) {
        return;
    }


    if (Cookies.get("toc") === "on") {
        blogTocEl.css("display", "block");
        toggleBtn.css("right", "");
        toggleBtn.css("display", "block");
    } else {
        toggleBtn.css("display", "none");
        toggleBtn.css("right", "10px");
    }

    toggleBtn.bind("click", function (e) {
        var preDisplay = blogTocEl.css("display");

         blogTocEl.toggle("slide");

        if (preDisplay == null || preDisplay === "none") {
            // blogTocEl.css("display", "block");
            // blogTocEl.show("slide", { direction: "left" });
            // toggleBtn.html(">>");
            toggleBtn.css("right", "");
            Cookies.set("toc", "on");
        } else {
            // blogTocEl.css("display", "none");
            // blogTocEl.hide("slide", { direction: "right" });
            // toggleBtn.html("<<");
            toggleBtn.css("right", "10px");
            Cookies.set("toc", "off");
        }
    });

    var header = $("#header");
    var toggleHeaderMem = Cookies.get("toggleHeader") ;

    var toggleHeader = toggleHeaderMem == null || toggleHeaderMem === "on";

    document.onmousemove = function (event) {
        var y = event.clientY;
        var view = getViewport();

        if (toggleHeader) {
            if (y <= 60) {
                header.slideDown(200);
            } else {
                header.slideUp(200);
            }
        }

        if (y > view.height - 40) {
            toggleBtn.css("display", "block");
        } else if (blogTocEl.css("display") === "none") {
            toggleBtn.css("display", "none");
        }
    };

    header.bind("dblclick", function (e) {
        toggleHeader = ! toggleHeader;

        if (toggleHeader) {
            Cookies.set("toggleHeader", "on");
        } else {
            Cookies.set("toggleHeader", "off");
        }
    })
}


function init() {
    initSearch();
    initJumpPage();
    initToc();
}


if (document.readyState === "complete" ||
    (document.readyState !== "loading" && !document.documentElement.doScroll)
) {
  init();
} else {
  document.addEventListener("DOMContentLoaded", init());
}

