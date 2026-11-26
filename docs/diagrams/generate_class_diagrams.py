import re, sys, os
ROOT='/home/claude/final_mobile'
S='/tmp/claude-0/-home-claude/66633bff-00e3-569b-b91b-92d476f35b75/scratchpad/gen'
files=[l.strip() for l in open(S+'/tv3_java.txt')]
extra=['app/src/main/java/com/example/ck_mobile/ui/base/BaseSecureActivity.java','app/src/main/java/com/example/ck_mobile/model/UserProfile.java']
EXCL={'Constants','MoneyUtils','ImageUtils','UiText','RepoCallback','ChatActions','ChatIntent','ChatPrefs','ServiceCatalog','AuditRepository'}
decl=re.compile(r'^\s*(?:public\s+|abstract\s+|final\s+)*(class|interface|enum)\s+(\w+)(?:<[^>]*>)?(?:\s+extends\s+([\w.]+)(?:<[^>]*>)?)?(?:\s+implements\s+([\w.,\s<>]+?))?\s*\{',re.M)
types={}   # name -> dict
src={}
total_all=0
for f in files+extra:
    s=open(f'{ROOT}/{f}',encoding='utf-8').read()
    src[f]=s
    allm=re.findall(r'^\s*(?:public\s+|private\s+|protected\s+|abstract\s+|final\s+|static\s+)*(?:class|interface|enum)\s+\w+',s,re.M)
    if f in files: total_all+=len(allm)
    m=decl.search(s)
    if not m: continue
    kind,name,ext,impl=m.group(1),m.group(2),m.group(3),m.group(4)
    abstract=bool(re.search(r'\babstract\s+class\s+'+name,s))
    pk=f.split('/')[-2]
    if pk=='adapter': pk=f.split('/')[-3]+'_adapter'
    pubs=[]
    for mm in re.finditer(r'^\s*(?:public\s+)(?:static\s+)?(?:final\s+)?(?:abstract\s+)?[\w<>\[\],.? ]+?\s+(\w+)\s*\(',s,re.M):
        n=mm.group(1)
        if n==name or n.startswith('on') or n in pubs or n in('equals','hashCode','toString'): continue
        pubs.append(n)
    types[name]=dict(file=f,kind=kind,ext=ext,impl=[i.strip().split('<')[0] for i in impl.split(',')] if impl else [],abstract=abstract,pk=pk,pubs=pubs,src=s,tv3=f in files)
print('declared types in TV3 files (incl nested):',total_all,file=sys.stderr)

import copy
SETS={'class_officer': ['BaseSecureActivity', 'CustomerListActivity', 'CustomerFormActivity', 'InterestRateActivity', 'EkycListActivity', 'EkycReviewActivity', 'EkycRegisterActivity', 'KycStatusActivity', 'CustomerAdapter', 'KycAdapter', 'CustomerListViewModel', 'CustomerRepository', 'KycRepository', 'RateRepository', 'SeedRepository', 'AuditRepository', 'UserProfile', 'KycRecord', 'InterestRate'], 'class_customer': ['BaseSecureActivity', 'TransactionHistoryActivity', 'ServiceOrderActivity', 'TransactionAdapter', 'ServiceItemAdapter', 'TransactionHistoryViewModel', 'TransactionRepository', 'ServiceOrderRepository', 'ServiceCatalog', 'TransactionRecord', 'ServiceItem', 'Account'], 'class_chat': ['BaseSecureActivity', 'ChatActivity', 'ChatAdapter', 'ChatViewModel', 'ChatEngine', 'RuleBasedChatEngine', 'GeminiChatEngine', 'HybridChatEngine', 'IntentMatcher', 'BotReply', 'ChatPrefs', 'ChatActions', 'ChatRepository', 'ChatMessage', 'DeepLinkRouter', 'RateRepository', 'TransactionRepository']}
for NAME,NODES in SETS.items():
    shown=[n for n in NODES if n in types]

    def layer(t):
        pk=t['pk']
        if pk=='officer': return 'Officer UI'
        if pk=='customer': return 'Customer UI'
        if pk.endswith('_adapter'): return 'Adapter'
        if pk=='viewmodel': return 'ViewModel'
        if pk=='repository': return 'Repository'
        if pk=='chat': return 'Chat'
        if pk=='model': return 'Model'
        if pk=='base': return 'Base'
        if pk=='utils': return 'Utils'
        return pk
    col={'Officer UI':'#FCE4EC','Customer UI':'#FCE4EC','Adapter':'#F8BBD0','ViewModel':'#E3F2FD','Repository':'#E8F5E9','Chat':'#FFF8E1','Model':'#F3E5F5','Base':'#ECEFF1','Utils':'#ECEFF1'}
    edges={}  # (a,b,kind)
    def add(a,b,k):
        if a==b or a not in shown or b not in shown: return
        pri={'inherit':0,'realize':0,'comp':1,'assoc':2,'dep':3}
        for kk in list(edges):
            if kk[0]==a and kk[1]==b:
                if pri[kk[2]]<=pri[k]: return
                del edges[kk]
        edges[(a,b,k)]=1
    for a,t in types.items():
        s=t['src']
        body=re.sub(r'^import .*$','',s,flags=re.M)
        if t['ext'] and t['ext'] in types: add(a,t['ext'],'inherit')
        for i in t['impl']:
            if i in types: add(a,i,'realize')
        for m in re.finditer(r'(?:private|protected)\s+(?:final\s+)?(\w+)(?:<[^>]*>)?\s+\w+\s*=\s*new\s+(\w+)',body):
            add(a,m.group(2),'comp')
        for m in re.finditer(r'(?:private|protected)\s+(?:final\s+)?(\w+)(?:<([\w, ]*)>)?\s+\w+\s*(?:;|=)',body):
            add(a,m.group(1),'assoc')
            if m.group(2):
                for g in re.split(r'[ ,]+',m.group(2)): add(a,g,'assoc')
        for b in shown:
            if b==a: continue
            if re.search(r'\b'+b+r'\b',body):
                add(a,b,'dep')
    # prune noisy deps: keep dep only to Model/BotReply/ServiceItem... from non-UI, and UI->ViewModel/Repository
    keep={}
    for (a,b,k) in edges:
        la,lb=layer(types[a]),layer(types[b])
        if k=='dep' and not (lb=='Model' or b in ('BotReply','AuditRepository','DeepLinkRouter')): continue
        if k=='dep' and la not in ('Repository','Chat','Utils'): continue
        if k=='dep' and lb in('Adapter','Base') : continue
        if k=='dep' and la in('Officer UI','Customer UI') and lb!='Repository' and lb!='ViewModel' and b not in('DeepLinkRouter',): continue
        keep[(a,b,k)]=1
    edges=keep
    order=['Base','Officer UI','Customer UI','Adapter','ViewModel','Repository','Chat','Model','Utils']
    out=['digraph G { rankdir=TB; compound=true; newrank=true; fontname="DejaVu Sans"; nodesep=0.35; ranksep=0.7; splines=true;',
     'node [shape=plain fontname="DejaVu Sans" fontsize=10]; edge [fontname="DejaVu Sans" fontsize=9 color="#555555"];']
    for L in order:
        ns=[n for n in shown if layer(types[n])==L]
        if not ns: continue
        out.append(f'subgraph "cluster_{L}" {{ label="{L}"; style="rounded,filled"; fillcolor="#FAFAFA"; color="#BDBDBD"; fontsize=12;')
        for n in ns:
            t=types[n]
            stereo='«interface»' if t['kind']=='interface' else ('«abstract»' if t['abstract'] else ('«enum»' if t['kind']=='enum' else ''))
            rows=''
            if L not in('Officer UI','Customer UI','Adapter','Model'):
                rows=''.join(f'<TR><TD ALIGN="LEFT" BORDER="0">+ {p}()</TD></TR>' for p in t['pubs'][:5])
                if len(t['pubs'])>5: rows+='<TR><TD ALIGN="LEFT" BORDER="0">…</TD></TR>'
            head=f'<TR><TD BGCOLOR="{col[L]}"><B>{n}</B>' + (f'<BR/><I>{stereo}</I>' if stereo else '') + '</TD></TR>'
            out.append(f'"{n}" [label=<<TABLE BORDER="1" CELLBORDER="0" CELLSPACING="0" CELLPADDING="4">{head}{rows}</TABLE>>];')
        out.append('}')
    style={'inherit':'arrowhead=empty','realize':'arrowhead=empty style=dashed','comp':'arrowhead=none arrowtail=diamond dir=back','assoc':'arrowhead=vee','dep':'arrowhead=vee style=dashed color="#9E9E9E"'}
    for (a,b,k) in edges:
        out.append(f'"{a}" -> "{b}" [{style[k]}];')
    out.append('}')
    open(S+'/'+NAME+'.dot','w',encoding='utf-8').write('\n'.join(out))
    print(NAME,'nodes',len(shown),'edges',len(edges),file=sys.stderr)
    from collections import Counter
    print(Counter(k for _,_,k in edges),file=sys.stderr)
