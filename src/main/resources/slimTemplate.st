<div style="background:none repeat scroll 0 0;border-color:#FF8000;border:1px solid gray;">
    <a href="javascript:toggleCollapsable('$className$');">
		<b><img src="/files/images/collapsableClosed.gif" class="left" id="img$className$">$className$</b>
	</a>
	<span id="$className$" class="hidden">
        <br /><br />
        <table cellspacing="0" border="1">
            <tbody>
                <tr>
                    <td>script</td>
                    <td colspan="$colspan$">$className$</td>
                </tr>
                $methods: {method|<tr>
                    $method:{c|<td colspan="$c.colspan$">$c.name$</td>
        }$</tr>}$
            </tbody>
        </table>
        <br />
    </span>
</div>
<br/>