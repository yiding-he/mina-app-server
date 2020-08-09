function containsKeyword(text, keyword) {
    if (text.length < keyword.length) {
        return false;
    }

    var keywordIndex, textIndex = 0;
    for (keywordIndex = 0; keywordIndex < keyword.length; keywordIndex++) {
        var ch = keyword.charAt(keywordIndex);
        textIndex = text.indexOf(ch, textIndex);
        if (textIndex === -1) {
            return false;
        }
    }

    return true;
}

$(function () {
    var keywordChanged = function (event) {
        var keyword = $.trim($('#filter').val());

        var first = true;
        $('.functions>.function').each(function () {
            var t = $(this);

            if (keyword === '') {
                t.removeClass('highlighted').show();
            } else {
                var title = t.find('.function-name>a').html();
                var description = t.find('.description').html();
                if (containsKeyword(title, keyword) ||
                        containsKeyword(description, keyword)) {
                    t.show();
                    if (first) {
                        t.addClass('highlighted');
                        first = false;
                    } else {
                        t.removeClass('highlighted');
                    }
                } else {
                    t.hide();
                }
            }
        });
    };

    var keydown = function(event) {
        var highlighted;

        if (event.keyCode === 9) {
            event.preventDefault();

            highlighted = $('.functions>.function.highlighted:visible');

            if (highlighted.size() === 0) {
                $($('.functions>.function:visible').get(0)).addClass('highlighted');
            } else {
                var t = $(highlighted.get(0));
                t.removeClass('highlighted');

                var next = t.nextAll(':visible');
                if (next.size() > 0) {
                    $(next.get(0)).addClass('highlighted');
                } else {
                    $($('.functions>.function:visible').get(0)).addClass('highlighted');
                }
            }

            return false;
        } else if (event.keyCode === 13) {

            highlighted = $('.functions>.function.highlighted:visible');

            if (highlighted.size() > 0) {
                window.location = $(highlighted.get(0)).find('a').attr('href');
            }
            return false;
        }

        return keywordChanged(event);
    };

    var keyup = function(event) {
        if (event.keyCode === 9) {
            return false;
        } else if (event.keyCode === 13) {
            return false;
        }

        return keywordChanged(event);
    };

    var selectAll = function () {
        this.select()
    };

    $('#filter').keydown(keydown).keyup(keyup).focus(selectAll).click(selectAll);
});