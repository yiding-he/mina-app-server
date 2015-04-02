$(function () {
    var keywordChanged = function (event) {
        var keyword = $.trim($('#filter').val());

        var first = true;
        $('.functions>.function').each(function () {
            var t = $(this);

            if (keyword == '') {
                t.removeClass('highlighted').show();
            } else {
                var title = t.find('.function-name>a').html();
                var description = t.find('.description').html();
                if (title.toLowerCase().indexOf(keyword.toLowerCase()) != -1 ||
                    description.toLowerCase().indexOf(keyword.toLowerCase()) != -1) {
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

        if (event.keyCode == 9) {
            event.preventDefault();

            highlighted = $('.functions>.function.highlighted:visible');

            if (highlighted.size() == 0) {
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
        } else if (event.keyCode == 13) {

            highlighted = $('.functions>.function.highlighted:visible');

            if (highlighted.size() > 0) {
                window.location = $(highlighted.get(0)).find('a').attr('href');
            }
            return false;
        }

        return keywordChanged(event);
    };

    var keyup = function(event) {
        if (event.keyCode == 9) {
            return false;
        } else if (event.keyCode == 13) {
            return false;
        }

        return keywordChanged(event);
    };

    var selectAll = function () {
        this.select()
    };

    $('#filter').keydown(keydown).keyup(keyup).focus(selectAll).click(selectAll);
});